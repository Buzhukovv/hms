//package housingManagment.hms.config;
//
//import housingManagment.hms.entities.userEntity.BaseUser;
//import housingManagment.hms.service.userService.CustomUserDetailsService;
//import housingManagment.hms.service.userService.BaseUserService; // Ensure this import matches your package structure
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//import java.io.IOException;
//import java.util.Optional;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//        private final CustomUserDetailsService userDetailsService;
//        private final PasswordEncoder passwordEncoder;
//        private final BaseUserService baseUserService;
//
//        @Bean
//        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//                http
//                        .cors() // <--- Добавь это
//                        .and()
//                        .authorizeHttpRequests(auth -> auth
//                                // Swagger and API docs endpoints
//                                .requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/webjars/**")
//                                .permitAll()
//
//                                // Static resources
//                                .requestMatchers("/css/**", "/js/**", "/images/**", "/error")
//                                .permitAll()
//
//                                // Authentication endpoints
//                                .requestMatchers("/login", "/logout")
//                                .permitAll()
//
//                                // API endpoints
//                                .requestMatchers("/api/**")
//                                .authenticated()
//
//                                // All other requests require authentication
//                                .anyRequest()
//                                .authenticated()
//                        )
//                        .formLogin(form -> form
//                                .loginPage("/login")
//                                .defaultSuccessUrl("/", true)
//                                .failureUrl("/login?error=true")
//                                .successHandler(authenticationSuccessHandler())
//                                .permitAll()
//                        )
//                        .logout(logout -> logout
//                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                                .logoutSuccessUrl("/login?logout=true")
//                                .invalidateHttpSession(true)
//                                .clearAuthentication(true)
//                                .deleteCookies("JSESSIONID")
//                                .permitAll()
//                        )
//                        .rememberMe(rememberMe -> rememberMe
//                                .key("uniqueAndSecretKey")
//                                .tokenValiditySeconds(86400) // 1 day
//                        )
//                        // Enable CSRF protection, but disable for API and Swagger endpoints
//                        .csrf(csrf -> csrf
//                                .ignoringRequestMatchers("/api/**", "/swagger-ui/**")
//                        );
//
//                return http.build();
//        }
//
//        @Bean
//        public AuthenticationSuccessHandler authenticationSuccessHandler() {
//                return new AuthenticationSuccessHandler() {
//                        @Override
//                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                                            Authentication authentication) throws IOException, ServletException {
//                                HttpSession session = request.getSession();
//                                String email = authentication.getName();
//
//                                try {
//                                        // Unwrap the Optional<BaseUser> and throw an exception if the user is not found
//                                        BaseUser user = baseUserService.findByEmail(email);
//                                        String fullName = user.getFirstName() + " " + user.getLastName();
//                                        session.setAttribute("userFullName", fullName);
//                                } catch (Exception e) {
//                                        // Log the error and continue with authentication
//                                        System.err.println("Error setting user full name in session: " + e.getMessage());
//                                }
//
//                                response.sendRedirect("/");
//                        }
//                };
//        }
//
//        @Bean
//        public AuthenticationProvider authenticationProvider() {
//                return new CustomAuthenticationProvider(userDetailsService, passwordEncoder);
//        }
//
//        @Bean
//        public AuthenticationManager authenticationManager() {
//                return new ProviderManager(authenticationProvider());
//        }
//}