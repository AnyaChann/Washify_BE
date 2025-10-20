package com.washify.apis.controller;

import com.washify.apis.dto.request.ForgotPasswordRequest;
import com.washify.apis.dto.request.ResetPasswordRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "🔑 Password Management", description = "Quên mật khẩu, đổi mật khẩu, reset password")
public class PasswordResetController {
    
    private final PasswordResetService passwordResetService;
    
    /**
     * Bước 1: Request forgot password (gửi email với token)
     * POST /api/auth/forgot-password
     * Public endpoint - không cần authentication
     */
    @PostMapping("/forgot-password")
    @Operation(
        summary = "🌐 Quên mật khẩu (Bước 1)", 
        description = """
            **Access:** 🌐 Public - Không cần authentication
            
            Gửi email reset password cho user quên mật khẩu.
            
            **Email Verification:**
            - Check format email
            - Check disposable email (block)
            - Check MX records (domain tồn tại)
            
            **Flow:**
            1. User nhập email
            2. System verify email
            3. Tạo token (30 phút)
            4. Gửi email với link reset
            
            **Security:**
            - Không tiết lộ email có tồn tại hay không
            - Luôn return success message
            - Token one-time use
            
            **Response:**
            - Success message (dù email có tồn tại hay không)
            """
    )
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
    @Operation(
        summary = "🌐 Validate token reset (Bước 2)", 
        description = """
            **Access:** 🌐 Public - Không cần authentication
            
            Kiểm tra token reset password còn hợp lệ không.
            
            **Use Case:**
            - Frontend check token khi user click link trong email
            - Hiển thị form reset password nếu valid
            - Hiển thị error nếu expired/invalid
            
            **Validations:**
            - Token tồn tại trong DB
            - Token chưa hết hạn (30 phút)
            - Token chưa được sử dụng
            
            **Response:**
            - true: Token hợp lệ, cho phép reset
            - false: Token expired/invalid
            """
    )
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
    @Operation(
        summary = "🌐 Reset mật khẩu (Bước 3)", 
        description = """
            **Access:** 🌐 Public - Không cần authentication
            
            Reset password với token từ email.
            
            **Flow:**
            1. User nhập password mới + confirm
            2. System validate token
            3. Check password match
            4. Update password (BCrypt hash)
            5. Mark token as used
            6. User có thể đăng nhập với password mới
            
            **Validations:**
            - Token valid (chưa hết hạn, chưa dùng)
            - Password >= 8 ký tự
            - Password match confirm password
            
            **Security:**
            - Token one-time use
            - Password hashed with BCrypt
            - Old tokens deleted after success
            
            **Response:**
            - Success: Đổi password thành công
            - Error: Token invalid hoặc passwords không match
            """
    )
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
