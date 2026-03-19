package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Lấy toàn bộ tin nhắn giữa 2 người (cả 2 chiều)
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(m.sender.userId = :user1 AND m.receiver.userId = :user2) OR " +
           "(m.sender.userId = :user2 AND m.receiver.userId = :user1) " +
           "ORDER BY m.sentAt ASC")
    List<ChatMessage> findConversation(@Param("user1") Long user1, @Param("user2") Long user2);

    // Lấy danh sách customer đã từng chat (dành cho salesstaff xem inbox)
    @Query("SELECT DISTINCT m.sender FROM ChatMessage m WHERE m.receiver.userId = :staffId AND m.sender.userRole = 'CUSTOMER'")
    List<Group1.ShoesOnlineShop.entity.User> findCustomersThatChatted(@Param("staffId") Long staffId);

    // Đếm tin chưa đọc
    long countByReceiverUserIdAndIsReadFalse(Long receiverId);
}
