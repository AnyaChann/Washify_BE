package com.washify.apis.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho request tạo/cập nhật khuyến mãi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {
    
    @NotBlank(message = "Mã khuyến mãi không được để trống")
    @Size(max = 50, message = "Mã khuyến mãi không quá 50 ký tự")
    private String code;
    
    @Size(max = 255, message = "Mô tả không quá 255 ký tự")
    private String description;
    
    @NotNull(message = "Loại giảm giá không được để trống")
    private String discountType; // PERCENT, FIXED
    
    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal discountValue;
    
    private LocalDateTime startDate; // Ngày bắt đầu
    
    private LocalDateTime endDate; // Ngày kết thúc
    
    private Boolean isActive = true; // Trạng thái hoạt động
}
