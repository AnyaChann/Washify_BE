package com.washify.apis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration - Cấu hình cho phép frontend gọi API
 * 
 * TESTING MODE: Cho phép tất cả origins
 * Production: Chỉ định cụ thể origins
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // TESTING: Cho phép tất cả origins
                .allowedOriginPatterns("*")
                
                // PRODUCTION: Uncomment và chỉ định origins cụ thể
                // .allowedOrigins(
                //         "http://localhost:3000",
                //         "http://localhost:4200",
                //         "http://localhost:5173",
                //         "https://your-production-domain.com"
                // )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
