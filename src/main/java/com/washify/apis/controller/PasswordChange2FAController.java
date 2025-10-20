package com.washify.apis.controller;

import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.service.PasswordChange2FAService;
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
public class PasswordChange2FAController {

    private final PasswordChange2FAService passwordChange2FAService;

    /**
     * Validate token bật/tắt 2FA (để hiện trang confirm)
     * GET /api/auth/security/2fa-toggle/validate?token={token}
     * Public endpoint
     */
    @GetMapping("/validate")
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
    public ResponseEntity<ApiResponse<Void>> confirm2FAToggle(@RequestParam String token) {
        passwordChange2FAService.confirm2FAToggle(token);
        return ResponseEntity.ok(
            ApiResponse.success("Cài đặt bảo mật 2 lớp đã được cập nhật thành công")
        );
    }
}
