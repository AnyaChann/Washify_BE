package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO cho response thông tin user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Boolean isActive;
    private Boolean requireEmailVerificationForPasswordChange; // Bảo mật 2 lớp cho đổi password
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long branchId;
    private String branchName;
    private Set<String> roles; // Danh sách tên roles
}
