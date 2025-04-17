package housingManagment.hms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000") // Укажи адрес фронта
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Или "*", если разрешаешь все
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
