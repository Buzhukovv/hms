package housingManagment.hms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration to redirect Swagger UI requests to home page in production
 */
@Configuration
public class SwaggerRedirectConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect any Swagger UI requests to home page
        registry.addRedirectViewController("/swagger-ui.html", "/");
        registry.addRedirectViewController("/swagger-ui/**", "/");
        registry.addRedirectViewController("/v3/api-docs/**", "/");
    }
}