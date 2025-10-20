package com.washify.apis.controller;

import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.service.PasswordChangeService;
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
public class PasswordChangeController {
    
    private final PasswordChangeService passwordChangeService;
    
    /**
     * Validate password change token
     * GET /api/auth/password-change/validate?token={token}
     * Public endpoint
     */
    @GetMapping("/validate")
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
    public ResponseEntity<ApiResponse<Void>> confirmPasswordChange(@RequestParam String token) {
        passwordChangeService.confirmPasswordChange(token);
        return ResponseEntity.ok(ApiResponse.success(
            "Đổi mật khẩu thành công. Bạn có thể đăng nhập với mật khẩu mới."
        ));
    }
}
