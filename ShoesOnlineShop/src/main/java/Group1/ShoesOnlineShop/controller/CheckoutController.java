package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.*;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import Group1.ShoesOnlineShop.repository.OrderRepository;
import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private CouponRepository couponRepository;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // Check if it's an OAuth2 user
            if (auth.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
                return userRepository.findByProviderId(auth.getName())
                        .or(() -> {
                            org.springframework.security.oauth2.core.user.OAuth2User oauth2User = 
                                (org.springframework.security.oauth2.core.user.OAuth2User) auth.getPrincipal();
                            String email = oauth2User.getAttribute("email");
                            return userRepository.findByUserEmail(email);
                        }).orElse(null);
            }
            return userRepository.findByUserName(auth.getName()).orElse(null);
        }
        return null;
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to checkout.");
            return "redirect:/login"; // Assuming /login handles the form auth
        }

        List<Cart> cartItems = cartService.getCartItems(user.getUserName(), session.getId());
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty.");
            return "redirect:/cart";
        }

        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", total);
        model.addAttribute("user", user); // Pass user to pre-fill standard shipping info
        model.addAttribute("coupons", userCouponService.getAvailableCoupons(user.getUserId()));
        return "customer-checkout";
    }

    @PostMapping("/checkout/process")
    public String processCheckout(@RequestParam("paymentMethod") String paymentMethod,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("address") String address,
                                  @RequestParam(value = "couponId", required = false) Long couponId,
                                  HttpSession session,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        List<Cart> cartItems = cartService.getCartItems(user.getUserName(), session.getId());
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        try {
            Coupon coupon = null;
            if (couponId != null) {
                coupon = couponRepository.findById(couponId).orElse(null);
            }

            Order order = orderService.createOrderFromCart(user, cartItems, phone, address, paymentMethod, coupon);
            
            if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                String vnpayUrl = vnPayService.createOrder(request, order.getTotalAmount().longValue(), "Payment for order " + order.getOrderId(), baseUrl + "/vnpay/return");
                return "redirect:" + vnpayUrl;
            } else {
                // Cash on delivery
                cartService.clearCart(user.getUserName(), session.getId());
                
                // Mark coupon as used if applied
                if (order.getCoupon() != null) {
                    userCouponService.markAsUsed(user.getUserId(), order.getCoupon().getId());
                }

                redirectAttributes.addFlashAttribute("successMessage", "Order created successfully (COD).");
                return "redirect:/payment-result?status=success&orderId=" + order.getOrderId();
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing checkout: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/vnpay/return")
    public String vnpayReturn(HttpServletRequest request, Model model, HttpSession session) {
        int paymentStatus = vnPayService.orderReturn(request);
        
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String transactionId = request.getParameter("vnp_TransactionNo");
        // Extract orderId from vnp_OrderInfo e.g. "Payment for order 15"
        Long orderId = null;
        if (orderInfo != null && orderInfo.contains("order ")) {
            try {
                String idStr = orderInfo.substring(orderInfo.lastIndexOf(" ") + 1);
                orderId = Long.parseLong(idStr.trim());
            } catch (Exception ignored) { }
        }

        if (paymentStatus == 1 && orderId != null) {
            // Success
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.setPaymentStatus("PAID");
                order.setOrderStatus("CONFIRMED"); // Move from PENDING to CONFIRMED
                if (order.getPayments() != null && !order.getPayments().isEmpty()) {
                    Payment p = order.getPayments().get(0);
                    p.setPaymentStatus("SUCCESS");
                    p.setTransactionId(transactionId);
                }
                orderRepository.save(order);
                
                // Clear cart only on success
                User user = order.getUser();
                if (user != null) {
                    cartService.clearCart(user.getUserName(), session.getId());
                    
                    // Mark coupon as used if applied
                    if (order.getCoupon() != null) {
                        userCouponService.markAsUsed(user.getUserId(), order.getCoupon().getId());
                    }
                }
            }
            return "redirect:/payment-result?status=success&orderId=" + orderId;
        } else {
            // Fail
            if (orderId != null) {
                Order order = orderRepository.findById(orderId).orElse(null);
                if (order != null) {
                    order.setPaymentStatus("FAILED");
                    if (order.getPayments() != null && !order.getPayments().isEmpty()) {
                        Payment p = order.getPayments().get(0);
                        p.setPaymentStatus("FAILED");
                        p.setTransactionId(transactionId);
                    }
                    orderRepository.save(order);
                }
            }
            return "redirect:/payment-result?status=failed&orderId=" + (orderId != null ? orderId : "");
        }
    }

    @GetMapping("/payment-result")
    public String paymentResult(@RequestParam(name = "status", required = false) String status,
                                @RequestParam(name = "orderId", required = false) Long orderId,
                                Model model) {
        model.addAttribute("status", status);
        model.addAttribute("orderId", orderId);
        return "customer-payment-result";
    }
}
