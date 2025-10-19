package com.washify.apis.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho request tạo/cập nhật dịch vụ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {
    
    @NotBlank(message = "Tên dịch vụ không được để trống")
    @Size(max = 100, message = "Tên dịch vụ không quá 100 ký tự")
    private String name;
    
    private String description; // Mô tả dịch vụ
    
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;
    
    private Integer estimatedTime; // Thời gian ước tính (giờ)
    
    private Boolean isActive = true; // Trạng thái hoạt động
}
