package Group1.ShoesOnlineShop.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("errorMessage", "Error: File size too large! Please upload an image smaller than 20MB.");
        
        // Lấy lại URL trước đó để quay lại đúng form
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contains("/save")) {
            return "redirect:" + referer;
        }
        return "redirect:/sliders";
    }
}
