package com.washify.apis.controller;

import com.washify.apis.dto.request.ChangePasswordRequest;
import com.washify.apis.dto.request.UserRegistrationRequest;
import com.washify.apis.dto.request.UserUpdateRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.UserResponse;
import com.washify.apis.entity.User;
import com.washify.apis.service.PasswordChange2FAService;
import com.washify.apis.service.PasswordChangeService;
import com.washify.apis.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến User
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Quản lý user, profile, roles")
public class UserController {
    
    private final UserService userService;
    private final PasswordChangeService passwordChangeService;
    private final PasswordChange2FAService passwordChange2FAService;
    
    /**
     * Lấy thông tin user theo ID
     * GET /api/users/{id}
     * Admin/Staff xem tất cả, User chỉ xem chính mình
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "Lấy thông tin user thành công"));
    }
    
    /**
     * Lấy thông tin user theo email
     * GET /api/users/email/{email}
     * Chỉ Admin và Staff
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user, "Lấy thông tin user thành công"));
    }
    
    /**
     * Lấy danh sách tất cả users
     * GET /api/users
     * Chỉ Admin và Staff
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Lấy danh sách user thành công"));
    }
    
    /**
     * Cập nhật thông tin user
     * PUT /api/users/{id}
     * Admin hoặc chính user đó
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(user, "Cập nhật thông tin thành công"));
    }
    
    /**
     * Xóa user
     * DELETE /api/users/{id}
     * Chỉ Admin
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa user thành công"));
    }
    
    /**
     * Gán role cho user
     * POST /api/users/{id}/roles/{roleName}
     * Chỉ Admin
     */
    @PostMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> assignRole(
            @PathVariable Long id,
            @PathVariable String roleName) {
        UserResponse user = userService.assignRole(id, roleName);
        return ResponseEntity.ok(ApiResponse.success(user, "Gán role thành công"));
    }
    
    /**
     * Đổi mật khẩu (với tùy chọn email verification)
     * POST /api/users/{id}/change-password
     * Admin hoặc chính user đó
     * 
     * Flow:
     * - Nếu user BẬT bảo mật 2 lớp → Gửi email xác nhận
     * - Nếu user TẮT bảo mật 2 lớp → Đổi ngay lập tức
     */
    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        
        // Get user để check setting
        User user = userService.getUserEntityById(id);
        
        // Check xem user có bật bảo mật 2 lớp không
        if (Boolean.TRUE.equals(user.getRequireEmailVerificationForPasswordChange())) {
            // MODE 1: Gửi email xác nhận (bảo mật cao)
            passwordChangeService.requestPasswordChange(id, request);
            return ResponseEntity.ok(ApiResponse.success(
                "Email xác nhận đã được gửi. Vui lòng kiểm tra hộp thư để hoàn tất việc đổi mật khẩu."
            ));
        } else {
            // MODE 2: Đổi ngay lập tức (nhanh chóng)
            userService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success(
                "Đổi mật khẩu thành công."
            ));
        }
    }
    
    /**
     * Request bật/tắt bảo mật 2 lớp cho việc đổi password (Gửi email xác nhận)
     * PUT /api/users/{id}/security/password-change-2fa
     * Admin hoặc chính user đó
     */
    @PutMapping("/{id}/security/password-change-2fa")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<Void>> togglePasswordChange2FA(
            @PathVariable Long id,
            @RequestParam boolean enable) {
        
        passwordChange2FAService.request2FAToggle(id, enable);
        
        String message = "Email xác nhận đã được gửi. Vui lòng kiểm tra hộp thư để hoàn tất việc " 
            + (enable ? "bật" : "tắt") + " bảo mật 2 lớp.";
        
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    
    // ========================================
    // PHASE 3: ADVANCED SEARCH & FILTERING
    // ========================================
    
    /**
     * Tìm kiếm users theo nhiều tiêu chí
     * GET /api/users/search
     * Chỉ Admin và Staff
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(
        summary = "Tìm kiếm users theo nhiều tiêu chí",
        description = "Search với username, email, fullName, roleId. Tất cả parameters đều optional. Chỉ ADMIN và STAFF."
    )
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) Long roleId) {
        List<UserResponse> users = userService.searchUsers(username, email, fullName, roleId);
        return ResponseEntity.ok(ApiResponse.success(users, "Tìm kiếm users thành công"));
    }
    
    /**
     * Lấy users theo role
     * GET /api/users/role/{roleId}
     * Chỉ Admin và Staff
     */
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(
        summary = "Lấy users theo role",
        description = "Lấy danh sách users có role cụ thể. Chỉ ADMIN và STAFF."
    )
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable Long roleId) {
        List<UserResponse> users = userService.getUsersByRole(roleId);
        return ResponseEntity.ok(ApiResponse.success(users, "Lấy danh sách users thành công"));
    }
    
    /**
     * Lấy chỉ users đang hoạt động (không bị soft delete)
     * GET /api/users/active
     * Chỉ Admin và Staff
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(
        summary = "Lấy users đang hoạt động",
        description = "Lấy danh sách users chưa bị xóa (deleted_at IS NULL). Chỉ ADMIN và STAFF."
    )
    public ResponseEntity<ApiResponse<List<UserResponse>>> getActiveUsers() {
        List<UserResponse> users = userService.getActiveUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Lấy danh sách users hoạt động thành công"));
    }
}

