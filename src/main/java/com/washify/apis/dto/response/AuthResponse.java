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
    
    @Builder.Default
    private String type = "Bearer"; // Token type
    
    private Long userId;
    private String email;
    private String fullName;
    private Set<String> roles;
}
