package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.config.VNPayConfig;
import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.service.CustomerOrderService;
import Group1.ShoesOnlineShop.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private CustomerOrderService customerOrderService;

    @PostMapping("/create")
    public String createPayment(
            @RequestParam String shippingAddress,
            @RequestParam String phone,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Validations
        if (!phone.matches("^[0-9]+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại chỉ được chứa số.");
            return "redirect:/cart";
        }
        if (!shippingAddress.matches("^[\\p{L}0-9\\s.,\\-]+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Địa chỉ nhà chỉ được chứa chữ, số, dấu phẩy, dấu chấm và dấu gạch ngang.");
            return "redirect:/cart";
        }

        String sessionId = session.getId();
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "vui lòng đăng nhập để thanh toán");
            return "redirect:/cart";
        }

        // Tạo đơn hàng nháp trong database để lấy ID (Lưu ý: Status mặc định là PENDING)
        Order savedOrder = customerOrderService.placeOrder(userId, sessionId, shippingAddress, phone);
        
        if(savedOrder == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tạo đơn hàng, vui lòng kiểm tra lại giỏ hàng.");
            return "redirect:/cart";
        }

        // Tạo URL VNPay
        long totalAmount = savedOrder.getTotalAmount().longValue(); // Giả định TotalAmount đã là tiền Việt chẵn
        String orderInfo = "Thanh toan don hang " + savedOrder.getOrderId();
        String ipAddress = VNPayConfig.getIpAddress(request);

        String paymentUrl = vnPayService.createPaymentUrl(totalAmount, orderInfo, ipAddress, savedOrder.getOrderId());

        return "redirect:" + paymentUrl; // Redirect to VNPAY port
    }

    @GetMapping("/vnpay-return")
    public String paymentReturn(HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        int paymentStatus = vnPayService.orderReturn(request);
        
        String transactionId = request.getParameter("vnp_TransactionNo");
        String orderIdStr = request.getParameter("vnp_TxnRef");
        
        Long orderId = Long.parseLong(orderIdStr);

        if (paymentStatus == 1) {
            // Success
            customerOrderService.updateOrderStatus(orderId, "PAID_ONLINE");
            redirectAttributes.addFlashAttribute("message", "Thanh toán thành công! Mã giao dịch: " + transactionId);
        } else {
            // Failed signature or failed transaction
            redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán thất bại hoặc đã bị huỷ.");
            customerOrderService.updateOrderStatus(orderId, "FAILED_ONLINE");
        }
        
        return "redirect:/orders/detail/" + orderIdStr;
    }
}
