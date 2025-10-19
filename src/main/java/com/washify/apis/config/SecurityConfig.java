package com.washify.apis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration cho Security
 * Tạm thời chỉ có PasswordEncoder, sẽ bổ sung JWT và Security config sau
 */
@Configuration
public class SecurityConfig {
    
    /**
     * Bean để mã hóa password
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
