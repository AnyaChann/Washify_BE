package com.washify.apis.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request đăng nhập
 * Hỗ trợ login bằng Username, Email hoặc Phone
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Username/Email/Phone không được để trống")
    private String username; // Có thể là username, email hoặc phone number
    
    @NotBlank(message = "Password không được để trống")
    private String password;
}
