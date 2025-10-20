package com.washify.apis.controller;

import com.washify.apis.dto.request.BranchRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.BranchResponse;
import com.washify.apis.service.BranchService;
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
     * Chỉ Admin
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
}
