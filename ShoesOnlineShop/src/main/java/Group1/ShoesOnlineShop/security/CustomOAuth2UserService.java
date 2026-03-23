package Group1.ShoesOnlineShop.security;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.HashSet;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService; // Added this import
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    // This handles OIDC (Google default)
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        try {
            return (OidcUser) processOAuth2User(userRequest, oidcUser);
        } catch (Exception e) {
            throw new OAuth2AuthenticationException(e.getMessage());
        }
    }

    // This handles standard OAuth2 (if openid scope is not used)
    public OAuth2User loadStandardUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        return processOAuth2User(userRequest, oauth2User);
    }
    
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String givenName = oauth2User.getAttribute("given_name");
        String familyName = oauth2User.getAttribute("family_name");
        String providerId = oauth2User.getAttribute("sub");
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from Google");
        }

        Optional<User> userOptional = userRepository.findByUserEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            boolean changed = false;
            if (user.getAuthProvider() == null) {
                user.setAuthProvider(provider);
                user.setProviderId(providerId);
                changed = true;
            }
            if (changed) {
                userRepository.save(user);
            }
        } else {
            // Register as new customer
            user = new User();
            
            // Set full name (try given + family first, then name, then email)
            String fullName = name;
            if (fullName == null || fullName.isEmpty()) {
                if (givenName != null && familyName != null) {
                    fullName = givenName + " " + familyName;
                } else if (givenName != null) {
                    fullName = givenName;
                } else {
                    fullName = email;
                }
            }
            user.setFullName(fullName);
            user.setUserEmail(email);
            
            // Generate unique username within 50 chars
            String baseUsername = email.split("@")[0];
            if (baseUsername.length() > 40) baseUsername = baseUsername.substring(0, 40);
            user.setUserName(baseUsername + "_g" + (System.currentTimeMillis() % 1000));
            
            user.setUserRole("CUSTOMER");
            user.setAuthProvider(provider);
            user.setProviderId(providerId);
            user.setIsActive(true);
            String placeholderPassword = "OAUTH2_USER_SECRET";
            user.setPasswordHash(placeholderPassword); 
            
            try {
                userRepository.save(user);
            } catch (Exception e) {
                e.printStackTrace();
                throw new OAuth2AuthenticationException("Could not register user from Google: " + e.getMessage());
            }
        }

        // Create new authorities including ROLE_CUSTOMER
        Collection<GrantedAuthority> mappedAuthorities = new HashSet<>(oauth2User.getAuthorities());
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        if (oauth2User instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) oauth2User;
            String nameAttributeKey = userRequest.getClientRegistration()
                    .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
            if (nameAttributeKey == null) nameAttributeKey = "sub";
            
            return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), nameAttributeKey);
        }

        return new DefaultOAuth2User(mappedAuthorities, oauth2User.getAttributes(), "email");
    }
}
