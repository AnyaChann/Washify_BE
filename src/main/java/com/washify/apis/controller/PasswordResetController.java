package com.washify.apis.controller;

import com.washify.apis.dto.request.ForgotPasswordRequest;
import com.washify.apis.dto.request.ResetPasswordRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý reset password qua email
 * Không cần authentication vì user quên mật khẩu
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {
    
    private final PasswordResetService passwordResetService;
    
    /**
     * Bước 1: Request forgot password (gửi email với token)
     * POST /api/auth/forgot-password
     * Public endpoint - không cần authentication
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        
        try {
            passwordResetService.createPasswordResetToken(request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(
                "Email reset password đã được gửi. Vui lòng kiểm tra hộp thư của bạn."));
        } catch (Exception e) {
            // Không tiết lộ email có tồn tại hay không (security)
            return ResponseEntity.ok(ApiResponse.success(
                "Nếu email tồn tại trong hệ thống, bạn sẽ nhận được email reset password."));
        }
    }
    
    /**
     * Bước 2: Validate token có hợp lệ không
     * GET /api/auth/reset-password/validate?token={token}
     * Public endpoint - để frontend check token trước khi show form
     */
    @GetMapping("/reset-password/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateResetToken(
            @RequestParam String token) {
        
        boolean isValid = passwordResetService.validatePasswordResetToken(token);
        
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success(true, 
                "Token hợp lệ"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                "Token không hợp lệ hoặc đã hết hạn"));
        }
    }
    
    /**
     * Bước 3: Reset password với token
     * POST /api/auth/reset-password
     * Public endpoint - user chưa đăng nhập
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        
        // Validate password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Mật khẩu xác nhận không khớp"));
        }
        
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success(
                "Đổi mật khẩu thành công. Bạn có thể đăng nhập với mật khẩu mới."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
