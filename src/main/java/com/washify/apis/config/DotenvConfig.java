package com.washify.apis.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * DotenvConfig - Load environment variables from .env file OR OS environment
 * 
 * Purpose:
 * - Development: Tự động load từ file .env (giống Node.js)
 * - Production (Railway, Heroku, etc.): Dùng OS environment variables
 * - File .env phải nằm ở root project (cùng cấp với pom.xml)
 * 
 * How it works:
 * 1. Spring khởi động → @Configuration được load
 * 2. @PostConstruct method được gọi
 * 3. Kiểm tra OS environment variables trước
 * 4. Nếu có DATABASE_URL trong OS env → skip .env file (Production mode)
 * 5. Nếu không → load từ .env file (Development mode)
 * 
 * Priority Order:
 * 1. OS Environment Variables (Railway, Heroku set qua dashboard)
 * 2. .env file (local development)
 * 3. application.properties defaults
 * 
 * Railway.com / Cloud Platforms:
 * - Railway tự động inject env vars vào OS environment
 * - KHÔNG cần file .env trên production
 * - Set variables qua Railway dashboard: Settings → Variables
 * 
 * Security:
 * - File .env KHÔNG được commit lên Git
 * - Production dùng Railway/Heroku dashboard để set env vars
 */
@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadDotenv() {
        // Kiểm tra xem đã có OS environment variables chưa
        String osEnvCheck = System.getenv("DATABASE_URL");
        
        if (osEnvCheck != null && !osEnvCheck.isEmpty()) {
            // Production mode: Railway/Heroku đã inject env vars
            System.out.println("✅ [DotenvConfig] Running in PRODUCTION mode - Using OS environment variables");
            System.out.println("   DATABASE_URL found in OS environment");
            
            // Load tất cả OS env vars vào System.properties để Spring Boot có thể đọc
            System.getenv().forEach((key, value) -> {
                if (System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            });
            
            return; // Skip .env file loading
        }
        
        // Development mode: Load từ .env file
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")           // Tìm .env ở root project
                    .ignoreIfMissing()         // Không crash nếu không có .env
                    .load();

            // Load tất cả biến từ .env vào System.properties
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Chỉ set nếu chưa có trong System.properties
                if (System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            });

            System.out.println("✅ [DotenvConfig] Running in DEVELOPMENT mode - Loaded " + dotenv.entries().size() + " variables from .env file");

        } catch (Exception e) {
            System.out.println("⚠️  [DotenvConfig] No .env file found - Using application.properties defaults");
            System.out.println("   This is OK for Railway/Heroku deployments (they use OS environment variables)");
        }
    }
}
