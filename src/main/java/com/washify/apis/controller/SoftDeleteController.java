package com.washify.apis.controller;

import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.UserResponse;
import com.washify.apis.entity.*;
import com.washify.apis.service.SoftDeleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller xử lý các operations liên quan đến Soft Delete
 * Endpoints: /deleted, /restore, /permanent-delete
 */
@RestController
@RequestMapping("/api/soft-delete")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SoftDeleteController {
    
    private final SoftDeleteService softDeleteService;
    
    // ========================================
    // USER ENDPOINTS
    // ========================================
    
    /**
     * Lấy danh sách users đã bị xóa mềm
     */
    @GetMapping("/users/deleted")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getDeletedUsers() {
        List<User> users = softDeleteService.getDeletedUsers();
        List<UserResponse> response = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, 
            "Lấy danh sách users đã xóa thành công"));
    }
    
    /**
     * Khôi phục user đã bị xóa mềm
     */
    @PutMapping("/users/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreUser(@PathVariable Long id) {
        boolean restored = softDeleteService.restoreUser(id);
        if (restored) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "User đã được khôi phục thành công"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể khôi phục user. ID không tồn tại hoặc user chưa bị xóa"));
    }
    
    /**
     * Xóa vĩnh viễn user - CẢNH BÁO: Không thể khôi phục!
     */
    @DeleteMapping("/users/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteUser(@PathVariable Long id) {
        boolean deleted = softDeleteService.permanentlyDeleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "User đã được xóa vĩnh viễn"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể xóa vĩnh viễn user. ID không tồn tại"));
    }
    
    // ========================================
    // BRANCH ENDPOINTS
    // ========================================
    
    @GetMapping("/branches/deleted")
    public ResponseEntity<ApiResponse<List<Branch>>> getDeletedBranches() {
        List<Branch> branches = softDeleteService.getDeletedBranches();
        return ResponseEntity.ok(ApiResponse.success(branches, 
            "Lấy danh sách branches đã xóa thành công"));
    }
    
    @PutMapping("/branches/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreBranch(@PathVariable Long id) {
        boolean restored = softDeleteService.restoreBranch(id);
        if (restored) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "Branch đã được khôi phục thành công"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể khôi phục branch"));
    }
    
    @DeleteMapping("/branches/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteBranch(@PathVariable Long id) {
        boolean deleted = softDeleteService.permanentlyDeleteBranch(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "Branch đã được xóa vĩnh viễn"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể xóa vĩnh viễn branch"));
    }
    
    // ========================================
    // SERVICE ENDPOINTS
    // ========================================
    
    @GetMapping("/services/deleted")
    public ResponseEntity<ApiResponse<List<com.washify.apis.entity.Service>>> getDeletedServices() {
        List<com.washify.apis.entity.Service> services = softDeleteService.getDeletedServices();
        return ResponseEntity.ok(ApiResponse.success(services, 
            "Lấy danh sách services đã xóa thành công"));
    }
    
    @PutMapping("/services/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreService(@PathVariable Long id) {
        boolean restored = softDeleteService.restoreService(id);
        if (restored) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "Service đã được khôi phục thành công"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể khôi phục service"));
    }
    
    @DeleteMapping("/services/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteService(@PathVariable Long id) {
        boolean deleted = softDeleteService.permanentlyDeleteService(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "Service đã được xóa vĩnh viễn"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể xóa vĩnh viễn service"));
    }
    
    // ========================================
    // ORDER ENDPOINTS
    // ========================================
    
    @GetMapping("/orders/deleted")
    public ResponseEntity<ApiResponse<List<Order>>> getDeletedOrders() {
        List<Order> orders = softDeleteService.getDeletedOrders();
        return ResponseEntity.ok(ApiResponse.success(orders, 
            "Lấy danh sách orders đã xóa thành công"));
    }
    
    @PutMapping("/orders/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreOrder(@PathVariable Long id) {
        boolean restored = softDeleteService.restoreOrder(id);
        if (restored) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "Order đã được khôi phục thành công"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể khôi phục order"));
    }
    
    @DeleteMapping("/orders/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteOrder(@PathVariable Long id) {
        boolean deleted = softDeleteService.permanentlyDeleteOrder(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "Order đã được xóa vĩnh viễn"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể xóa vĩnh viễn order"));
    }
    
    // ========================================
    // PROMOTION ENDPOINTS
    // ========================================
    
    @GetMapping("/promotions/deleted")
    public ResponseEntity<ApiResponse<List<Promotion>>> getDeletedPromotions() {
        List<Promotion> promotions = softDeleteService.getDeletedPromotions();
        return ResponseEntity.ok(ApiResponse.success(promotions, 
            "Lấy danh sách promotions đã xóa thành công"));
    }
    
    @PutMapping("/promotions/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restorePromotion(@PathVariable Long id) {
        boolean restored = softDeleteService.restorePromotion(id);
        if (restored) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "Promotion đã được khôi phục thành công"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể khôi phục promotion"));
    }
    
    @DeleteMapping("/promotions/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeletePromotion(@PathVariable Long id) {
        boolean deleted = softDeleteService.permanentlyDeletePromotion(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "Promotion đã được xóa vĩnh viễn"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể xóa vĩnh viễn promotion"));
    }
    
    // ========================================
    // SHIPPER ENDPOINTS
    // ========================================
    
    @GetMapping("/shippers/deleted")
    public ResponseEntity<ApiResponse<List<Shipper>>> getDeletedShippers() {
        List<Shipper> shippers = softDeleteService.getDeletedShippers();
        return ResponseEntity.ok(ApiResponse.success(shippers, 
            "Lấy danh sách shippers đã xóa thành công"));
    }
    
    @PutMapping("/shippers/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreShipper(@PathVariable Long id) {
        boolean restored = softDeleteService.restoreShipper(id);
        if (restored) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "Shipper đã được khôi phục thành công"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể khôi phục shipper"));
    }
    
    @DeleteMapping("/shippers/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteShipper(@PathVariable Long id) {
        boolean deleted = softDeleteService.permanentlyDeleteShipper(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, 
                "Shipper đã được xóa vĩnh viễn"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(
            "Không thể xóa vĩnh viễn shipper"));
    }
    
    // ========================================
    // HELPER METHODS
    // ========================================
    
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
