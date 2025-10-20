package com.washify.apis.controller;

import com.washify.apis.dto.response.*;
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
@io.swagger.v3.oas.annotations.tags.Tag(name = "🗑️ Soft Delete", description = "Quản lý soft delete, restore - 🔒 Admin only")
public class SoftDeleteController {
    
    private final SoftDeleteService softDeleteService;
    
    // ========================================
    // USER ENDPOINTS
    // ========================================
    
    /**
     * Lấy danh sách users đã bị xóa mềm
     * Changed to /users (not /users/deleted) for consistency
     */
    @GetMapping("/users")
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
    
    @GetMapping("/branches")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getDeletedBranches() {
        List<Branch> branches = softDeleteService.getDeletedBranches();
        List<BranchResponse> response = branches.stream()
                .map(this::mapToBranchResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, 
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
    
    @GetMapping("/services")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getDeletedServices() {
        List<com.washify.apis.entity.Service> services = softDeleteService.getDeletedServices();
        List<ServiceResponse> response = services.stream()
                .map(this::mapToServiceResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, 
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
    
    /**
     * Lấy danh sách orders đã bị xóa mềm  
     * Changed to /orders (not /orders/deleted) for consistency
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getDeletedOrders() {
        List<Order> orders = softDeleteService.getDeletedOrders();
        List<OrderResponse> response = orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, 
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
    
    @GetMapping("/promotions")
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getDeletedPromotions() {
        List<Promotion> promotions = softDeleteService.getDeletedPromotions();
        List<PromotionResponse> response = promotions.stream()
                .map(this::mapToPromotionResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, 
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
    
    @GetMapping("/shippers")
    public ResponseEntity<ApiResponse<List<ShipperResponse>>> getDeletedShippers() {
        List<Shipper> shippers = softDeleteService.getDeletedShippers();
        List<ShipperResponse> response = shippers.stream()
                .map(this::mapToShipperResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, 
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
        response.setDeletedAt(user.getDeletedAt());
        return response;
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .userName(order.getUser() != null ? order.getUser().getFullName() : null)
                .branchId(order.getBranch() != null ? order.getBranch().getId() : null)
                .branchName(order.getBranch() != null ? order.getBranch().getName() : null)
                .orderDate(order.getOrderDate())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .build();
    }
    
    private ServiceResponse mapToServiceResponse(com.washify.apis.entity.Service service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .estimatedTime(service.getEstimatedTime())
                .isActive(service.getIsActive())
                .deletedAt(service.getDeletedAt())
                .build();
    }
    
    private BranchResponse mapToBranchResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .managerName(branch.getManagerName())
                .isActive(branch.getIsActive())
                .createdAt(branch.getCreatedAt())
                .deletedAt(branch.getDeletedAt())
                .build();
    }
    
    private PromotionResponse mapToPromotionResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .code(promotion.getCode())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType().name())
                .discountValue(promotion.getDiscountValue())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .isActive(promotion.getIsActive())
                .deletedAt(promotion.getDeletedAt())
                .build();
    }
    
    private ShipperResponse mapToShipperResponse(Shipper shipper) {
        return ShipperResponse.builder()
                .id(shipper.getId())
                .name(shipper.getName())
                .phone(shipper.getPhone())
                .vehicleNumber(shipper.getVehicleNumber())
                .isActive(shipper.getIsActive())
                .createdAt(shipper.getCreatedAt())
                .updatedAt(shipper.getUpdatedAt())
                .deletedAt(shipper.getDeletedAt())
                .build();
    }
}
