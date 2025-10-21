package com.washify.apis.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * DotenvConfig - Load environment variables from .env file
 * 
 * Purpose:
 * - Tự động load các biến môi trường từ file .env khi app khởi động
 * - Giống cách Node.js sử dụng dotenv package
 * - File .env phải nằm ở root project (cùng cấp với pom.xml)
 * 
 * How it works:
 * 1. Spring khởi động → @Configuration được load
 * 2. @PostConstruct method được gọi sau khi bean được tạo
 * 3. Dotenv đọc file .env → load vào System.properties
 * 4. Spring đọc ${ENV_VAR} từ application.properties → tìm trong System.properties
 * 5. Nếu không có trong .env → dùng default value sau dấu ':'
 * 
 * Example:
 * File .env:
 *   DATABASE_URL=jdbc:mysql://production.com:3306/washify
 * 
 * File application.properties:
 *   spring.datasource.url=${DATABASE_URL:jdbc:mysql://localhost:3306/washify_db}
 * 
 * Result:
 *   - Có .env → dùng production.com
 *   - Không có .env → dùng localhost (default)
 * 
 * Security:
 * - File .env KHÔNG được commit lên Git
 * - Chỉ commit .env.example làm template
 * - Mỗi developer/server có .env riêng
 */
@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadDotenv() {
        try {
            // Load .env file từ root project
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")           // Tìm .env ở root project
                    .ignoreIfMissing()         // Không crash nếu không có .env
                    .load();

            // Load tất cả biến từ .env vào System.properties
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Chỉ set nếu chưa có trong System.properties
                // (OS environment variables có priority cao hơn)
                if (System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            });

            System.out.println("✅ [DotenvConfig] Loaded " + dotenv.entries().size() + " environment variables from .env file");

        } catch (Exception e) {
            System.out.println("⚠️  [DotenvConfig] Could not load .env file (this is OK if using OS environment variables): " + e.getMessage());
        }
    }
}
