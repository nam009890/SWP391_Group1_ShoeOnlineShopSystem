package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Cart;
import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.CartRepository;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Cart> getCartItems(String username, String sessionId) {
        if (username != null) {
            User user = userRepository.findByUserName(username).orElse(null);
            if (user != null) {
                // If the user has items in their session cart, merge them into their user cart
                mergeSessionCartToUserCart(sessionId, user);
                return cartRepository.findByUser_UserId(user.getUserId());
            }
        }
        return cartRepository.findBySessionId(sessionId);
    }

    public void addToCart(Long productId, Integer quantity, String username, String sessionId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (quantity > product.getStockQuantity()) {
            throw new RuntimeException("Not enough stock available");
        }

        Cart cartItem = null;
        User user = null;

        if (username != null) {
            user = userRepository.findByUserName(username).orElse(null);
            if (user != null) {
                cartItem = cartRepository.findByUser_UserIdAndProduct_Id(user.getUserId(), productId);
            }
        } else {
            cartItem = cartRepository.findBySessionIdAndProduct_Id(sessionId, productId);
        }

        if (cartItem != null) {
            int newQuantity = cartItem.getQuantity() + quantity;
            if (newQuantity > product.getStockQuantity()) {
                throw new RuntimeException("Not enough stock available");
            }
            cartItem.setQuantity(newQuantity);
        } else {
            cartItem = new Cart();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            if (user != null) {
                cartItem.setUser(user);
            } else {
                cartItem.setSessionId(sessionId);
            }
        }
        
        cartRepository.save(cartItem);
    }

    public void updateCartItemQuantity(Long cartId, Integer quantity) {
        Cart cartItem = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity > cartItem.getProduct().getStockQuantity()) {
            throw new RuntimeException("Not enough stock available");
        }

        if (quantity <= 0) {
            cartRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartRepository.save(cartItem);
        }
    }

    public void removeCartItem(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    public void clearCart(String username, String sessionId) {
        if (username != null) {
            User user = userRepository.findByUserName(username).orElse(null);
            if (user != null) {
                cartRepository.deleteByUser_UserId(user.getUserId());
            }
        } else {
            cartRepository.deleteBySessionId(sessionId);
        }
    }

    private void mergeSessionCartToUserCart(String sessionId, User user) {
        if (sessionId == null) return;

        List<Cart> sessionCarts = cartRepository.findBySessionId(sessionId);
        if (sessionCarts != null && !sessionCarts.isEmpty()) {
            for (Cart sessionCart : sessionCarts) {
                Cart userCart = cartRepository.findByUser_UserIdAndProduct_Id(user.getUserId(), sessionCart.getProduct().getId());
                if (userCart != null) {
                    // Update quantity
                    userCart.setQuantity(userCart.getQuantity() + sessionCart.getQuantity());
                    cartRepository.save(userCart);
                } else {
                    // Re-assign to user
                    sessionCart.setSessionId(null);
                    sessionCart.setUser(user);
                    cartRepository.save(sessionCart);
                }
            }
            // All merged session items should not be found as session items anymore,
            // but just in case, this is handled by `setSessionId(null)`
        }
    }
}
