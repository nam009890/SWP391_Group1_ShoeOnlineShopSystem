package Group1.ShoesOnlineShop.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InternalAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        boolean isInternal = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) ||
                               "ROLE_SALE_STAFF".equals(a.getAuthority()) ||
                               "ROLE_MARKETING_STAFF".equals(a.getAuthority()));

        if (!isInternal) {
            request.getSession().invalidate();
            SecurityContextHolder.clearContext();
            response.sendRedirect(request.getContextPath() + "/internal/login?error");
            return;
        }

        String redirectUrl = request.getContextPath() + "/";
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                redirectUrl = request.getContextPath() + "/internal/admin/home";
                break;
            } else if ("ROLE_SALE_STAFF".equals(auth.getAuthority())) {
                redirectUrl = request.getContextPath() + "/internal/orders";
                break;
            } else if ("ROLE_MARKETING_STAFF".equals(auth.getAuthority())) {
                redirectUrl = request.getContextPath() + "/internal/MarketingHome";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
