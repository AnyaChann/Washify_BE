package com.washify.apis.controller;

import com.washify.apis.dto.request.PromotionRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.PromotionResponse;
import com.washify.apis.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến Promotion (mã giảm giá/khuyến mãi)
 */
@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
@Tag(name = "🎁 Promotions", description = "Quản lý mã giảm giá/khuyến mãi - 👔 Staff/Admin, 🌐 Public (xem)")
public class PromotionController {
    
    private final PromotionService promotionService;
    
    /**
     * Tạo promotion mới
     * POST /api/promotions
     * Chỉ Staff và Admin
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Tạo mã giảm giá mới", description = "**Access:** 👔 Staff/Admin/Manager\n\nTạo mã khuyến mãi mới cho hệ thống")
    public ResponseEntity<ApiResponse<PromotionResponse>> createPromotion(@Valid @RequestBody PromotionRequest request) {
        PromotionResponse promotion = promotionService.createPromotion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(promotion, "Tạo mã khuyến mãi thành công"));
    }
    
    /**
     * Lấy promotion theo ID
     * GET /api/promotions/{id}
     * Public - ai cũng có thể xem
     */
    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết mã giảm giá", description = "**Access:** 🌐 Public\n\nXem thông tin chi tiết về mã khuyến mãi")
    public ResponseEntity<ApiResponse<PromotionResponse>> getPromotionById(@PathVariable Long id) {
        PromotionResponse promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Lấy thông tin khuyến mãi thành công"));
    }
    
    /**
     * Lấy promotion theo code
     * GET /api/promotions/code/{code}
     * Public - ai cũng có thể xem
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "Tìm mã giảm giá theo code", description = "**Access:** 🌐 Public\n\nTìm kiếm khuyến mãi bằng mã code (VD: SUMMER2025)")
    public ResponseEntity<ApiResponse<PromotionResponse>> getPromotionByCode(@PathVariable String code) {
        PromotionResponse promotion = promotionService.getPromotionByCode(code);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Lấy thông tin khuyến mãi thành công"));
    }
    
    /**
     * Lấy tất cả promotions
     * GET /api/promotions
     * Chỉ Staff và Admin
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Lấy tất cả mã giảm giá", description = "**Access:** 👔 Staff/Admin/Manager\n\nLấy danh sách tất cả mã khuyến mãi (bao gồm inactive và expired)")
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getAllPromotions() {
        List<PromotionResponse> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(ApiResponse.success(promotions, "Lấy danh sách khuyến mãi thành công"));
    }
    
    /**
     * Lấy các promotions đang active
     * GET /api/promotions/active
     * Public - ai cũng có thể xem
     */
    @GetMapping("/active")
    @Operation(summary = "Lấy mã giảm giá đang active", description = "**Access:** 🌐 Public\n\nLấy danh sách các mã khuyến mãi đang hoạt động")
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getActivePromotions() {
        List<PromotionResponse> promotions = promotionService.getActivePromotions();
        return ResponseEntity.ok(ApiResponse.success(promotions, "Lấy danh sách khuyến mãi active thành công"));
    }
    
    /**
     * Lấy các promotions đang valid (active và trong thời hạn)
     * GET /api/promotions/valid
     * Public - ai cũng có thể xem
     */
    @GetMapping("/valid")
    @Operation(summary = "Lấy mã giảm giá hợp lệ", description = "**Access:** 🌐 Public\n\nLấy danh sách các mã khuyến mãi đang hoạt động và trong thời hạn sử dụng")
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getValidPromotions() {
        List<PromotionResponse> promotions = promotionService.getValidPromotions();
        return ResponseEntity.ok(ApiResponse.success(promotions, "Lấy danh sách khuyến mãi hợp lệ thành công"));
    }
    
    /**
     * Validate promotion code
     * POST /api/promotions/validate
     * Public - customer cần validate trước khi áp dụng
     */
    @PostMapping("/validate")
    @Operation(summary = "Kiểm tra mã giảm giá", description = "**Access:** 🌐 Public\n\nKiểm tra xem mã khuyến mãi có hợp lệ không và tính số tiền giảm")
    public ResponseEntity<ApiResponse<PromotionService.PromotionValidationResponse>> validatePromotion(
            @RequestParam String code,
            @RequestParam BigDecimal orderAmount) {
        PromotionService.PromotionValidationResponse validation = 
            promotionService.validatePromotionCode(code, orderAmount);
        
        if (validation.isValid()) {
            return ResponseEntity.ok(ApiResponse.success(validation, "Mã khuyến mãi hợp lệ"));
        } else {
            return ResponseEntity.ok(ApiResponse.<PromotionService.PromotionValidationResponse>builder()
                    .success(false)
                    .message(validation.getMessage())
                    .data(validation)
                    .timestamp(java.time.LocalDateTime.now())
                    .build());
        }
    }
    
    /**
     * Lấy thống kê usage của promotion
     * GET /api/promotions/{id}/usage
     * Chỉ Staff và Admin
     */
    @GetMapping("/{id}/usage")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Thống kê sử dụng mã giảm giá", description = "**Access:** 👔 Staff/Admin/Manager\n\nXem số lần mã khuyến mãi đã được sử dụng")
    public ResponseEntity<ApiResponse<PromotionService.PromotionUsageResponse>> getPromotionUsage(@PathVariable Long id) {
        PromotionService.PromotionUsageResponse usage = promotionService.getPromotionUsage(id);
        return ResponseEntity.ok(ApiResponse.success(usage, "Lấy thống kê sử dụng thành công"));
    }
    
    /**
     * Cập nhật promotion
     * PUT /api/promotions/{id}
     * Chỉ Staff và Admin
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Cập nhật mã giảm giá", description = "**Access:** 👔 Staff/Admin/Manager\n\nCập nhật thông tin mã khuyến mãi")
    public ResponseEntity<ApiResponse<PromotionResponse>> updatePromotion(
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequest request) {
        PromotionResponse promotion = promotionService.updatePromotion(id, request);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Cập nhật khuyến mãi thành công"));
    }
    
    /**
     * Kích hoạt promotion
     * PATCH /api/promotions/{id}/activate
     * Chỉ Staff và Admin
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Kích hoạt mã giảm giá", description = "**Access:** 👔 Staff/Admin/Manager\n\nKích hoạt mã khuyến mãi")
    public ResponseEntity<ApiResponse<PromotionResponse>> activatePromotion(@PathVariable Long id) {
        PromotionResponse promotion = promotionService.activatePromotion(id);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Kích hoạt khuyến mãi thành công"));
    }
    
    /**
     * Vô hiệu hóa promotion
     * PATCH /api/promotions/{id}/deactivate
     * Chỉ Staff và Admin
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Vô hiệu hóa mã giảm giá", description = "**Access:** 👔 Staff/Admin/Manager\n\nVô hiệu hóa mã khuyến mãi (tạm ngưng sử dụng)")
    public ResponseEntity<ApiResponse<PromotionResponse>> deactivatePromotion(@PathVariable Long id) {
        PromotionResponse promotion = promotionService.deactivatePromotion(id);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Vô hiệu hóa khuyến mãi thành công"));
    }
    
    /**
     * Xóa promotion (soft delete)
     * DELETE /api/promotions/{id}
     * Chỉ Admin
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa mã giảm giá", description = "**Access:** 🔒 Admin only\n\nXóa mã khuyến mãi (soft delete - có thể restore)")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa khuyến mãi thành công"));
    }
}
