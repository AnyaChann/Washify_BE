package com.washify.apis.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO cho request tạo đơn hàng mới
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    
    @NotNull(message = "User ID không được để trống")
    private Long userId;
    
    private Long branchId; // Chi nhánh xử lý (nullable)
    
    @NotEmpty(message = "Đơn hàng phải có ít nhất một dịch vụ")
    @Valid
    private List<OrderItemRequest> items; // Danh sách dịch vụ
    
    private String notes; // Ghi chú thêm
    
    private List<String> promotionCodes; // Danh sách mã giảm giá
}
