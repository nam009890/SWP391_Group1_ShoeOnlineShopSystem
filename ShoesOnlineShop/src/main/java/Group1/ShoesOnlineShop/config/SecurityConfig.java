package Group1.ShoesOnlineShop.config;

import Group1.ShoesOnlineShop.security.CustomerAuthenticationSuccessHandler;
import Group1.ShoesOnlineShop.security.InternalAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomerAuthenticationSuccessHandler customerSuccessHandler;

    @Autowired
    private InternalAuthenticationSuccessHandler internalSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =============================================
    // INTERNAL FILTER CHAIN (ORDER 1)
    // Matches ALL /internal/** routes
    // =============================================
    @Bean
    @Order(1)
    public SecurityFilterChain internalSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/internal/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/internal/login").permitAll()
                .requestMatchers("/internal/admin/**").hasRole("ADMIN")
                .requestMatchers("/internal/orders/**", "/internal/invoices/**").hasAnyRole("SALE_STAFF", "ADMIN")
                .requestMatchers("/internal/MarketingHome/**", "/internal/sliders/**", "/internal/coupons/**", "/internal/contents/**").hasAnyRole("MARKETING_STAFF", "ADMIN")
                .requestMatchers("/internal/feedbacks/**").hasAnyRole("ADMIN", "SALE_STAFF", "MARKETING_STAFF")
                .anyRequest().hasAnyRole("ADMIN", "SALE_STAFF", "MARKETING_STAFF")
            )
            .formLogin(form -> form
                .loginPage("/internal/login")
                .loginProcessingUrl("/internal/login/process")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(internalSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/internal/logout")
                .logoutSuccessUrl("/internal/login?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // =============================================
    // CUSTOMER FILTER CHAIN (ORDER 2)
    // Matches everything NOT /internal/**
    // =============================================
    @Bean
    @Order(2)
    public SecurityFilterChain customerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**", "/uploads/**", "/", "/home").permitAll()
                .requestMatchers("/cart/**", "/checkout/**", "/profile/**", "/MyOrder/**").hasRole("CUSTOMER")
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login/process")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(customerSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
