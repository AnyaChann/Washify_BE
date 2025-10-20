package com.washify.apis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request tạo/cập nhật Role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    
    @NotBlank(message = "Tên role không được để trống")
    @Size(max = 50, message = "Tên role không quá 50 ký tự")
    private String name; // Tên role (VD: ADMIN, STAFF, CUSTOMER)
    
    @Size(max = 255, message = "Mô tả không quá 255 ký tự")
    private String description; // Mô tả vai trò
}
