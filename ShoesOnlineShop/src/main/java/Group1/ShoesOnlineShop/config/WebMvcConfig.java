package Group1.ShoesOnlineShop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // Upload directory relative to the project root — using absolute path to avoid
    // working-directory issues when running with mvnw or from IDE.
    public static final Path UPLOAD_DIR = Paths.get("d:/SWP_Project/New_Code/ShoesOnlineShop/uploads").toAbsolutePath().normalize();

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(UPLOAD_DIR.toUri().toString());
    }
}
