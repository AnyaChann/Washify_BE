package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho response thông tin Role
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    
    private Long id;
    private String name; // Tên role (VD: ADMIN, STAFF, CUSTOMER)
    private String description; // Mô tả vai trò
    private Long userCount; // Số lượng user có role này (optional)
}
