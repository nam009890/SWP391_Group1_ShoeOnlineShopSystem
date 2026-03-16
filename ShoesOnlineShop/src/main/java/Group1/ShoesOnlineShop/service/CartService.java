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
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Cart> getCartItems(Long userId, String sessionId) {
        if (userId != null) {
            return cartRepository.findByUserUserId(userId);
        } else {
            return cartRepository.findBySessionId(sessionId);
        }
    }

    public void addToCart(Long productId, Integer quantity, Long userId, String sessionId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;

        Cart cartItem = null;
        if (userId != null) {
            cartItem = cartRepository.findByUserUserIdAndProduct_Id(userId, productId);
        } else if (sessionId != null) {
            cartItem = cartRepository.findBySessionIdAndProduct_Id(sessionId, productId);
        }

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new Cart();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);
                cartItem.setUser(user);
            } else {
                cartItem.setSessionId(sessionId);
            }
        }
        cartRepository.save(cartItem);
    }

    public void updateQuantity(Long cartId, Integer quantity) {
        Cart cartItem = cartRepository.findById(cartId).orElse(null);
        if (cartItem != null) {
            if (quantity > 0) {
                cartItem.setQuantity(quantity);
                cartRepository.save(cartItem);
            } else {
                cartRepository.delete(cartItem);
            }
        }
    }

    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(Long userId, String sessionId) {
        if (userId != null) {
            cartRepository.deleteByUserUserId(userId);
        } else if (sessionId != null) {
            cartRepository.deleteBySessionId(sessionId);
        }
    }
}
