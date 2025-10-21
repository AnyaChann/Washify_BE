package com.washify.apis.service;

import com.washify.apis.annotation.Audited;
import com.washify.apis.dto.request.PromotionRequest;
import com.washify.apis.dto.response.PromotionResponse;
import com.washify.apis.entity.Order;
import com.washify.apis.entity.Promotion;
import com.washify.apis.repository.OrderPromotionRepository;
import com.washify.apis.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Promotion (mã giảm giá/khuyến mãi)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PromotionService {
    
    private final PromotionRepository promotionRepository;
    private final OrderPromotionRepository orderPromotionRepository;
    
    /**
     * Tạo promotion mới
     */
    @Audited(action = "CREATE_PROMOTION", entityType = "Promotion", description = "Tạo mã khuyến mãi mới")
    public PromotionResponse createPromotion(PromotionRequest request) {
        // Kiểm tra mã code đã tồn tại chưa
        if (promotionRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã khuyến mãi '" + request.getCode() + "' đã tồn tại");
        }
        
        Promotion promotion = new Promotion();
        promotion.setCode(request.getCode().toUpperCase()); // Chuyển về uppercase
        promotion.setDescription(request.getDescription());
        promotion.setDiscountType(Promotion.DiscountType.valueOf(request.getDiscountType()));
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        Promotion savedPromotion = promotionRepository.save(promotion);
        return mapToPromotionResponse(savedPromotion);
    }
    
    /**
     * Lấy promotion theo ID
     */
    @Transactional(readOnly = true)
    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id));
        return mapToPromotionResponse(promotion);
    }
    
    /**
     * Lấy promotion theo code
     */
    @Transactional(readOnly = true)
    public PromotionResponse getPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với mã: " + code));
        return mapToPromotionResponse(promotion);
    }
    
    /**
     * Lấy tất cả promotions
     */
    @Transactional(readOnly = true)
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::mapToPromotionResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy các promotions đang active
     */
    @Transactional(readOnly = true)
    public List<PromotionResponse> getActivePromotions() {
        return promotionRepository.findByIsActiveTrue().stream()
                .map(this::mapToPromotionResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy các promotions đang valid (active và trong thời hạn)
     */
    @Transactional(readOnly = true)
    public List<PromotionResponse> getValidPromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findValidPromotions(now).stream()
                .map(this::mapToPromotionResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật promotion
     */
    @Audited(action = "UPDATE_PROMOTION", entityType = "Promotion", description = "Cập nhật thông tin khuyến mãi")
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id));
        
        // Nếu đổi code, kiểm tra code mới đã tồn tại chưa
        if (!promotion.getCode().equalsIgnoreCase(request.getCode())) {
            if (promotionRepository.existsByCode(request.getCode())) {
                throw new RuntimeException("Mã khuyến mãi '" + request.getCode() + "' đã tồn tại");
            }
            promotion.setCode(request.getCode().toUpperCase());
        }
        
        promotion.setDescription(request.getDescription());
        promotion.setDiscountType(Promotion.DiscountType.valueOf(request.getDiscountType()));
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setIsActive(request.getIsActive());
        
        Promotion updatedPromotion = promotionRepository.save(promotion);
        return mapToPromotionResponse(updatedPromotion);
    }
    
    /**
     * Xóa promotion (soft delete)
     */
    @Audited(action = "DELETE_PROMOTION", entityType = "Promotion", description = "Xóa mềm khuyến mãi")
    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id));
        promotionRepository.delete(promotion); // Soft delete
    }
    
    /**
     * Kích hoạt promotion
     */
    @Audited(action = "ACTIVATE_PROMOTION", entityType = "Promotion", description = "Kích hoạt khuyến mãi")
    public PromotionResponse activatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id));
        
        promotion.setIsActive(true);
        Promotion updatedPromotion = promotionRepository.save(promotion);
        return mapToPromotionResponse(updatedPromotion);
    }
    
    /**
     * Vô hiệu hóa promotion
     */
    @Audited(action = "DEACTIVATE_PROMOTION", entityType = "Promotion", description = "Vô hiệu hóa khuyến mãi")
    public PromotionResponse deactivatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id));
        
        promotion.setIsActive(false);
        Promotion updatedPromotion = promotionRepository.save(promotion);
        return mapToPromotionResponse(updatedPromotion);
    }
    
    /**
     * Validate promotion code
     * Kiểm tra mã có hợp lệ không (active, trong thời hạn, v.v.)
     */
    @Transactional(readOnly = true)
    public PromotionValidationResponse validatePromotionCode(String code, BigDecimal orderAmount) {
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
                .orElse(null);
        
        if (promotion == null) {
            return new PromotionValidationResponse(false, "Mã khuyến mãi không tồn tại", null, null);
        }
        
        if (!promotion.getIsActive()) {
            return new PromotionValidationResponse(false, "Mã khuyến mãi đã bị vô hiệu hóa", null, null);
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        if (promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) {
            return new PromotionValidationResponse(false, "Mã khuyến mãi chưa đến thời gian áp dụng", null, null);
        }
        
        if (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
            return new PromotionValidationResponse(false, "Mã khuyến mãi đã hết hạn", null, null);
        }
        
        // Tính số tiền giảm
        BigDecimal discountAmount = calculateDiscount(promotion, orderAmount);
        
        return new PromotionValidationResponse(
            true, 
            "Mã khuyến mãi hợp lệ", 
            promotion.getId(),
            discountAmount
        );
    }
    
    /**
     * Tính số tiền giảm dựa trên promotion và order amount
     */
    private BigDecimal calculateDiscount(Promotion promotion, BigDecimal orderAmount) {
        if (promotion.getDiscountType() == Promotion.DiscountType.PERCENT) {
            // Giảm theo %
            return orderAmount.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100));
        } else {
            // Giảm cố định
            return promotion.getDiscountValue();
        }
    }
    
    /**
     * Đếm số lần promotion được sử dụng
     */
    @Transactional(readOnly = true)
    public long countPromotionUsage(Long promotionId) {
        return orderPromotionRepository.countByPromotionId(promotionId);
    }
    
    /**
     * Lấy thống kê usage của promotion
     */
    @Transactional(readOnly = true)
    public PromotionUsageResponse getPromotionUsage(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id));
        
        long usageCount = orderPromotionRepository.countByPromotionId(id);
        
        return new PromotionUsageResponse(
            promotion.getId(),
            promotion.getCode(),
            usageCount,
            promotion.getIsActive()
        );
    }
    
    /**
     * Map entity sang response DTO
     */
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
    
    /**
     * Inner class cho validation response
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class PromotionValidationResponse {
        private boolean valid;
        private String message;
        private Long promotionId;
        private BigDecimal discountAmount;
    }
    
    /**
     * Inner class cho usage response
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class PromotionUsageResponse {
        private Long promotionId;
        private String code;
        private long usageCount;
        private boolean isActive;
    }
}
