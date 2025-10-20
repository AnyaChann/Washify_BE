package com.washify.apis.controller;

import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.service.PasswordChange2FAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý xác nhận bật/tắt bảo mật 2 lớp cho việc đổi password
 * Endpoints public, xác thực qua token trong email
 */
@RestController
@RequestMapping("/api/auth/security/2fa-toggle")
@RequiredArgsConstructor
@Tag(name = "🔑 Password Management", description = "Quên mật khẩu, đổi mật khẩu, reset password")
public class PasswordChange2FAController {

    private final PasswordChange2FAService passwordChange2FAService;

    /**
     * Validate token bật/tắt 2FA (để hiện trang confirm)
     * GET /api/auth/security/2fa-toggle/validate?token={token}
     * Public endpoint
     */
    @GetMapping("/validate")
    @Operation(
        summary = "🌐 Validate token toggle 2FA", 
        description = """
            **Access:** 🌐 Public - Không cần authentication
            
            Validate token để bật/tắt 2FA setting.
            
            **Context:**
            - User request bật/tắt 2FA cho password change
            - System gửi email xác nhận
            - User click link trong email
            - Frontend call API này để validate
            
            **What is 2FA for Password Change?**
            - Khi BẬT: Đổi password phải xác nhận qua email
            - Khi TẮT: Đổi password ngay lập tức (chỉ cần password cũ)
            
            **Response:**
            - Success: Token valid
            - Error: Token expired/invalid
            """
    )
    public ResponseEntity<ApiResponse<Void>> validateToken(@RequestParam String token) {
        boolean isValid = passwordChange2FAService.validate2FAToggleToken(token);
        
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success("Token hợp lệ"));
        } else {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Token không hợp lệ hoặc đã hết hạn"));
        }
    }

    /**
     * Xác nhận bật/tắt bảo mật 2 lớp
     * POST /api/auth/security/2fa-toggle/confirm?token={token}
     * Public endpoint
     */
    @PostMapping("/confirm")
    @Operation(
        summary = "🌐 Xác nhận toggle 2FA", 
        description = """
            **Access:** 🌐 Public - Không cần authentication (verify qua token)
            
            Xác nhận bật/tắt 2FA cho password change.
            
            **Flow:**
            1. User request toggle 2FA (authenticated endpoint)
            2. System tạo token + send email
            3. User click link trong email
            4. Call API này để confirm
            5. 2FA setting được cập nhật
            
            **After Enable 2FA:**
            - User đổi password → Phải xác nhận qua email
            - Tăng bảo mật
            
            **After Disable 2FA:**
            - User đổi password → Đổi ngay (chỉ cần old password)
            - Tiện lợi hơn
            
            **Response:**
            - Success: Cập nhật 2FA setting thành công
            - Error: Token invalid/expired
            """
    )
    public ResponseEntity<ApiResponse<Void>> confirm2FAToggle(@RequestParam String token) {
        passwordChange2FAService.confirm2FAToggle(token);
        return ResponseEntity.ok(
            ApiResponse.success("Cài đặt bảo mật 2 lớp đã được cập nhật thành công")
        );
    }
}
