package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.ChatMessage;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.ChatMessageRepository;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Gửi tin nhắn từ sender đến receiver
     */
    public ChatMessage sendMessage(Long senderId, Long receiverId, String messageText) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        ChatMessage msg = new ChatMessage();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setMessage(messageText);
        msg.setIsRead(false);
        return chatMessageRepository.save(msg);
    }

    /**
     * Lấy toàn bộ cuộc trò chuyện giữa 2 người, đồng thời đánh dấu đã đọc
     */
    public List<ChatMessage> getConversation(Long user1, Long user2) {
        List<ChatMessage> messages = chatMessageRepository.findConversation(user1, user2);
        // Đánh dấu tin nhắn gửi tới user1 là đã đọc
        messages.stream()
                .filter(m -> m.getReceiver().getUserId().equals(user1) && !m.getIsRead())
                .forEach(m -> {
                    m.setIsRead(true);
                    chatMessageRepository.save(m);
                });
        return messages;
    }

    /**
     * Danh sách các customer đã chat với staff này
     */
    public List<User> getChatCustomers(Long staffId) {
        return chatMessageRepository.findCustomersThatChatted(staffId);
    }

    /**
     * Lấy danh sách tất cả sales staff (role = SALES_STAFF)
     */
    public List<User> getAllSalesStaff() {
        return userRepository.findAll().stream()
                .filter(u -> "SALES_STAFF".equals(u.getUserRole()) && Boolean.TRUE.equals(u.getIsActive()))
                .toList();
    }

    /**
     * Đếm tin nhắn chưa đọc cho user
     */
    public long countUnread(Long userId) {
        return chatMessageRepository.countByReceiverUserIdAndIsReadFalse(userId);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
}
