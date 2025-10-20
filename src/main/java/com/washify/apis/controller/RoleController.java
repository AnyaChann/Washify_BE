package com.washify.apis.controller;

import com.washify.apis.dto.request.RoleRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.RoleResponse;
import com.washify.apis.service.RoleService;
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
 * REST Controller xử lý các API liên quan đến Role (vai trò người dùng)
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "🔐 Roles", description = "Quản lý roles/phân quyền - 🔒 Admin only")
public class RoleController {
    
    private final RoleService roleService;
    
    /**
     * Tạo role mới
     * POST /api/roles
     * Chỉ Admin
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo role mới", description = "**Access:** 🔒 Admin only\n\nTạo vai trò người dùng mới (ADMIN, STAFF, CUSTOMER, etc.)")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        RoleResponse role = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(role, "Tạo role thành công"));
    }
    
    /**
     * Lấy role theo ID
     * GET /api/roles/{id}
     * Chỉ Admin và Staff
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Xem chi tiết role", description = "**Access:** 🔒 Admin/Staff\n\nXem thông tin chi tiết về một role")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success(role, "Lấy thông tin role thành công"));
    }
    
    /**
     * Lấy role theo name
     * GET /api/roles/name/{name}
     * Chỉ Admin và Staff
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Tìm role theo tên", description = "**Access:** 🔒 Admin/Staff\n\nTìm role bằng tên (VD: ADMIN, STAFF, CUSTOMER)")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleByName(@PathVariable String name) {
        RoleResponse role = roleService.getRoleByName(name);
        return ResponseEntity.ok(ApiResponse.success(role, "Lấy thông tin role thành công"));
    }
    
    /**
     * Lấy tất cả roles
     * GET /api/roles
     * Chỉ Admin và Staff
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Lấy tất cả roles", description = "**Access:** 🔒 Admin/Staff\n\nLấy danh sách tất cả vai trò trong hệ thống")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success(roles, "Lấy danh sách roles thành công"));
    }
    
    /**
     * Đếm số user có role này
     * GET /api/roles/{id}/user-count
     * Chỉ Admin
     */
    @GetMapping("/{id}/user-count")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Đếm user theo role", description = "**Access:** 🔒 Admin only\n\nĐếm số lượng user có role này")
    public ResponseEntity<ApiResponse<Long>> countUsersByRole(@PathVariable Long id) {
        long count = roleService.countUsersByRoleId(id);
        return ResponseEntity.ok(ApiResponse.success(count, "Lấy số lượng user thành công"));
    }
    
    /**
     * Cập nhật role
     * PUT /api/roles/{id}
     * Chỉ Admin
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật role", description = "**Access:** 🔒 Admin only\n\nCập nhật thông tin vai trò")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {
        RoleResponse role = roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success(role, "Cập nhật role thành công"));
    }
    
    /**
     * Xóa role
     * DELETE /api/roles/{id}
     * Chỉ Admin
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa role", description = "**Access:** 🔒 Admin only\n\nXóa vai trò (chỉ xóa được khi không còn user nào sử dụng)")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa role thành công"));
    }
}
