package com.washify.apis.controller;

import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.service.PasswordChangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller xử lý password change confirmation
 * Public endpoints - không cần authentication
 */
@RestController
@RequestMapping("/auth/password-change")
@RequiredArgsConstructor
@Tag(name = "🔑 Password Management", description = "Quên mật khẩu, đổi mật khẩu, reset password")
public class PasswordChangeController {
    
    private final PasswordChangeService passwordChangeService;
    
    /**
     * Validate password change token
     * GET /api/auth/password-change/validate?token={token}
     * Public endpoint
     */
    @GetMapping("/validate")
    @Operation(
        summary = "🌐 Validate token đổi mật khẩu", 
        description = """
            **Access:** 🌐 Public - Không cần authentication
            
            Kiểm tra token đổi mật khẩu (với 2FA) còn hợp lệ không.
            
            **Context:**
            - User đã bật 2FA cho password change
            - User request đổi password
            - System gửi email xác nhận
            - User click link trong email
            - Frontend call API này để validate token
            
            **Response:**
            - true: Token valid, cho phép confirm
            - false: Token expired/invalid
            """
    )
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String token) {
        boolean isValid = passwordChangeService.validatePasswordChangeToken(token);
        
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success(true, "Token hợp lệ"));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Token không hợp lệ hoặc đã hết hạn"));
        }
    }
    
    /**
     * Confirm password change
     * POST /api/auth/password-change/confirm?token={token}
     * Public endpoint
     */
    @PostMapping("/confirm")
    @Operation(
        summary = "🌐 Xác nhận đổi mật khẩu (2FA)", 
        description = """
            **Access:** 🌐 Public - Không cần authentication (verify qua token)
            
            Xác nhận đổi mật khẩu qua email (khi user bật 2FA).
            
            **Flow:**
            1. User request đổi password (có 2FA)
            2. System tạo token + send email
            3. User click link trong email
            4. Call API này với token
            5. Password được đổi
            
            **Security:**
            - Token one-time use
            - Token expires in 30 minutes
            - New password already hashed in token
            - Old token deleted after use
            
            **Response:**
            - Success: Đổi password thành công
            - Error: Token invalid/expired
            """
    )
    public ResponseEntity<ApiResponse<Void>> confirmPasswordChange(@RequestParam String token) {
        passwordChangeService.confirmPasswordChange(token);
        return ResponseEntity.ok(ApiResponse.success(
            "Đổi mật khẩu thành công. Bạn có thể đăng nhập với mật khẩu mới."
        ));
    }
}
