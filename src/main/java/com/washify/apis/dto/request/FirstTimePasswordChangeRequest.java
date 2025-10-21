package com.washify.apis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request đổi mật khẩu lần đầu (Guest User)
 * Không cần current password vì Guest User login với password mặc định
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirstTimePasswordChangeRequest {
    
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 50, message = "Mật khẩu mới phải từ 6-50 ký tự")
    private String newPassword;
    
    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
