package housingManagment.hms.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Custom authentication provider that supports both BCrypt and plain text
 * passwords
 */
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.debug("Authenticating user: {}", username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Check if password matches, with fallback to direct comparison for non-BCrypt
        // passwords
        boolean matches = false;
        String storedPassword = userDetails.getPassword();

        if (storedPassword != null && !storedPassword.startsWith("$2a$")) {
            // Plain text comparison for backward compatibility
            matches = password.equals(storedPassword);
        } else {
            // Use BCrypt for encoded passwords
            matches = passwordEncoder.matches(password, storedPassword);
        }

        if (matches) {
            log.debug("Authentication successful for user: {}", username);
            return new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities());
        } else {
            log.warn("Authentication failed for user: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}