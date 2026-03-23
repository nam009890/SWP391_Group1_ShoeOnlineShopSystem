package Group1.ShoesOnlineShop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("shoesonlineshop.group1@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            // Log error or handle as needed
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    public void sendVerificationCode(String to, String code) {
        String subject = "Password Reset Verification Code - ShoesOnlineShop";
        String body = "Your verification code is: " + code + "\n\nThis code will expire in 15 minutes.";
        sendEmail(to, subject, body);
    }
}
