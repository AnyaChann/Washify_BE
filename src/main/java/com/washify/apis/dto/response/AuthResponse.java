package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO cho response sau khi đăng nhập/đăng ký thành công
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token; // JWT token
    
    private String tokenType; // Token type (Bearer)
    
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private java.util.List<String> roles;
    private Boolean requirePasswordChange; // Bắt buộc đổi mật khẩu (dùng cho Guest User)
}
