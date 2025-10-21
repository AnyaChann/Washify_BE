package com.washify.apis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test để generate BCrypt hash cho passwords
 */
@SpringBootTest
public class PasswordHashGenerator {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    void generatePasswordHashes() {
        System.out.println("=".repeat(80));
        System.out.println("PASSWORD HASH GENERATOR FOR data.sql");
        System.out.println("=".repeat(80));
        System.out.println();
        
        // Password cho Admin/Manager/Staff/Shipper/Customer
        String defaultPassword = "washify123";
        String defaultHash = passwordEncoder.encode(defaultPassword);
        System.out.println("Default Password: " + defaultPassword);
        System.out.println("BCrypt Hash: " + defaultHash);
        System.out.println();
        
        // Password cho Guest Users
        String guestPassword = "Guest@123456";
        String guestHash = passwordEncoder.encode(guestPassword);
        System.out.println("Guest Password: " + guestPassword);
        System.out.println("BCrypt Hash: " + guestHash);
        System.out.println();
        
        System.out.println("=".repeat(80));
        System.out.println("Copy các hash này vào data.sql");
        System.out.println("=".repeat(80));
    }
}
