package edu.ntnu.idatt2106_09.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.logging.Logger;

/**
 * Configures Cross-Origin Resource Sharing (CORS) settings for the application.
 * This configuration allows the application to control which origins, methods, and headers are allowed in HTTP requests.
 */
@Slf4j
@Configuration
public class CorsConfig {

    /**
     * Configures a WebMvcConfigurer bean to set up CORS mappings.
     * The CORS configuration allows specific origins, methods, headers, and credentials for incoming requests.
     *
     * @return a configured WebMvcConfigurer instance for managing CORS settings.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                log.info("Setting up CORS configuration");
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173") // Replace with your frontend base URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}