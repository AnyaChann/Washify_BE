package com.washify.apis.controller;

import com.washify.apis.dto.request.UserRegistrationRequest;
import com.washify.apis.dto.request.UserUpdateRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.UserResponse;
import com.washify.apis.service.UserService;
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
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    /**
     * Đăng ký user mới
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "Đăng ký thành công"));
    }
    
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
}
