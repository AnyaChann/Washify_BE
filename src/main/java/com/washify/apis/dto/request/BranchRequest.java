package com.washify.apis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request tạo/cập nhật chi nhánh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequest {
    
    @NotBlank(message = "Tên chi nhánh không được để trống")
    @Size(max = 100, message = "Tên chi nhánh không quá 100 ký tự")
    private String name;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không quá 255 ký tự")
    private String address;
    
    @Size(max = 20, message = "Số điện thoại không quá 20 ký tự")
    private String phone;
    
    @Size(max = 100, message = "Tên quản lý không quá 100 ký tự")
    private String managerName;
}
