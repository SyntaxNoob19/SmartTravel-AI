package com.riya.smarttravel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;
    private final String frontendPath;

    public WebConfig(
            @Value("${app.cors.allowed-origins}") String allowedOrigins,
            @Value("${app.frontend.path}") String frontendPath) {
        this.allowedOrigins = allowedOrigins.split("\\s*,\\s*");
        this.frontendPath = frontendPath.endsWith("/") ? frontendPath : frontendPath + "/";
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept", "X-Requested-With")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve the frontend static files located outside the JAR (e.g., configured in app.frontend.path)
        // This ensures requests for HTML, CSS, JS, images, etc., are served correctly.
        registry.addResourceHandler("/**")
                .addResourceLocations(frontendPath);
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward root URL to the external index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }

}
