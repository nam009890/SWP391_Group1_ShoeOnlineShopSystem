package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.ChatMessage;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    // ─── CUSTOMER: Trang chat ─────────────────────────────────────────────────

    /**
     * Hiển thị trang chat cho customer.
     * URL: /chat  (optional ?staffId=...)
     */
    @GetMapping("/chat")
    public String customerChat(@RequestParam(required = false) Long staffId,
                               HttpSession session,
                               Model model) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<User> staffList = chatService.getAllSalesStaff();
        model.addAttribute("staffList", staffList);

        // Nếu chưa chọn staff, chọn người đầu tiên
        if (staffId == null && !staffList.isEmpty()) {
            staffId = staffList.get(0).getUserId();
        }

        if (staffId != null) {
            User selectedStaff = chatService.findUserById(staffId).orElse(null);
            model.addAttribute("selectedStaff", selectedStaff);

            List<ChatMessage> messages = chatService.getConversation(currentUser.getUserId(), staffId);
            model.addAttribute("messages", messages);
            model.addAttribute("selectedStaffId", staffId);
        }

        model.addAttribute("currentUser", currentUser);
        return "customer-chat";
    }

    /**
     * Customer gửi tin nhắn tới sales staff
     */
    @PostMapping("/chat/send")
    public String customerSendMessage(@RequestParam Long receiverId,
                                      @RequestParam String message,
                                      HttpSession session) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        chatService.sendMessage(currentUser.getUserId(), receiverId, message);
        return "redirect:/chat?staffId=" + receiverId;
    }

    // ─── SALES STAFF: Inbox và chat ─────────────────────────────────────────

    /**
     * Hiển thị inbox của sales staff (list customer đã chat)
     * URL: /staff/chat  (optional ?customerId=...)
     */
    @GetMapping("/staff/chat")
    public String staffChat(@RequestParam(required = false) Long customerId,
                            HttpSession session,
                            Model model) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<User> customers = chatService.getChatCustomers(currentUser.getUserId());
        model.addAttribute("customers", customers);

        if (customerId == null && !customers.isEmpty()) {
            customerId = customers.get(0).getUserId();
        }

        if (customerId != null) {
            User selectedCustomer = chatService.findUserById(customerId).orElse(null);
            model.addAttribute("selectedCustomer", selectedCustomer);

            List<ChatMessage> messages = chatService.getConversation(currentUser.getUserId(), customerId);
            model.addAttribute("messages", messages);
            model.addAttribute("selectedCustomerId", customerId);
        }

        model.addAttribute("currentUser", currentUser);
        return "staff-chat";
    }

    /**
     * Sales staff gửi tin nhắn tới customer
     */
    @PostMapping("/staff/chat/send")
    public String staffSendMessage(@RequestParam Long receiverId,
                                   @RequestParam String message,
                                   HttpSession session) {
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        chatService.sendMessage(currentUser.getUserId(), receiverId, message);
        return "redirect:/staff/chat?customerId=" + receiverId;
    }
}
