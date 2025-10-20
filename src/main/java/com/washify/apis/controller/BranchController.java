package com.washify.apis.controller;

import com.washify.apis.dto.request.BranchRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.BranchResponse;
import com.washify.apis.service.BranchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến Branch
 */
@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
@Tag(name = "Branches", description = "Quản lý chi nhánh - 🔒 Admin only")
public class BranchController {
    
    private final BranchService branchService;
    
    /**
     * Tạo chi nhánh mới
     * POST /api/branches
     * Chỉ Admin
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@Valid @RequestBody BranchRequest request) {
        BranchResponse branch = branchService.createBranch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(branch, "Tạo chi nhánh thành công"));
    }
    
    /**
     * Lấy thông tin chi nhánh theo ID
     * GET /api/branches/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchById(@PathVariable Long id) {
        BranchResponse branch = branchService.getBranchById(id);
        return ResponseEntity.ok(ApiResponse.success(branch, "Lấy thông tin chi nhánh thành công"));
    }
    
    /**
     * Lấy tất cả chi nhánh
     * GET /api/branches
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getAllBranches() {
        List<BranchResponse> branches = branchService.getAllBranches();
        return ResponseEntity.ok(ApiResponse.success(branches, "Lấy danh sách chi nhánh thành công"));
    }
    
    /**
     * Cập nhật chi nhánh
     * PUT /api/branches/{id}
     * Admin: toàn quyền cập nhật mọi chi nhánh
     * Manager: chỉ cập nhật chi nhánh mà họ quản lý
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @branchSecurity.isBranchManager(#id, authentication))")
    public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody BranchRequest request) {
        BranchResponse branch = branchService.updateBranch(id, request);
        return ResponseEntity.ok(ApiResponse.success(branch, "Cập nhật chi nhánh thành công"));
    }
    
    /**
     * Xóa chi nhánh
     * DELETE /api/branches/{id}
     * Chỉ Admin
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa chi nhánh thành công"));
    }
    
    // ========================================
    // ENHANCEMENTS - Phase 3: Statistics & Analytics
    // ========================================
    
    /**
     * Lấy thống kê tất cả chi nhánh (so sánh hiệu suất)
     * GET /api/branches/statistics
     * Admin/Staff/Manager
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<BranchService.BranchStatistics>>> getAllBranchStatistics() {
        List<BranchService.BranchStatistics> statistics = branchService.getAllBranchStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics, "Lấy thống kê chi nhánh thành công"));
    }
    
    /**
     * Lấy thống kê chi tiết của một chi nhánh
     * GET /api/branches/{id}/statistics
     * Admin/Staff/Manager
     */
    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<BranchService.BranchDetailStatistics>> getBranchDetailStatistics(@PathVariable Long id) {
        BranchService.BranchDetailStatistics statistics = branchService.getBranchDetailStatistics(id);
        return ResponseEntity.ok(ApiResponse.success(statistics, "Lấy thống kê chi tiết chi nhánh thành công"));
    }
    
    // ========================================
    // OPERATIONAL ENHANCEMENTS - Phase 3
    // ========================================
    
    /**
     * Tìm kiếm chi nhánh theo nhiều tiêu chí
     * GET /api/branches/search
     */
    @GetMapping("/search")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Tìm kiếm chi nhánh",
        description = "Tìm kiếm chi nhánh theo tên, địa chỉ hoặc trạng thái active. Tất cả parameters đều optional."
    )
    public ResponseEntity<ApiResponse<List<BranchResponse>>> searchBranches(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Boolean isActive) {
        List<BranchResponse> branches = branchService.searchBranches(name, address, isActive);
        return ResponseEntity.ok(ApiResponse.success(branches, "Tìm kiếm chi nhánh thành công"));
    }
    
    /**
     * Tìm chi nhánh gần vị trí người dùng
     * GET /api/branches/nearby
     */
    @GetMapping("/nearby")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Tìm chi nhánh gần đây",
        description = "Tìm các chi nhánh trong bán kính (km) từ vị trí hiện tại. Radius mặc định là 10km."
    )
    public ResponseEntity<ApiResponse<List<BranchResponse>>> findNearbyBranches(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10.0") Double radius) {
        List<BranchResponse> branches = branchService.findNearbyBranches(lat, lng, radius);
        return ResponseEntity.ok(ApiResponse.success(branches, 
            "Tìm thấy " + branches.size() + " chi nhánh trong bán kính " + radius + " km"));
    }
}

