package Group1.ShoesOnlineShop.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes, jakarta.servlet.http.HttpServletRequest request) {
        System.err.println("============ CRAZY ERROR ============");
        exc.printStackTrace();
        if (exc.getCause() != null) exc.getCause().printStackTrace();
        System.err.println("======================================");
        
        redirectAttributes.addFlashAttribute("errorMessage", "Error: File này lớn vượt mức cấu hình cho phép của Server!");
        
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contains("/save")) {
            return "redirect:" + referer;
        }
        return "redirect:/sliders";
    }
}
