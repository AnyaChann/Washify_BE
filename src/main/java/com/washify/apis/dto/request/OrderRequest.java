package com.washify.apis.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
    
    // Cho phép cả userId hoặc phoneNumber
    // - CUSTOMER tự đặt: userId = ID của họ (từ JWT token)
    // - STAFF tạo cho khách: phoneNumber (hệ thống tự tạo/tìm user)
    private Long userId;
    
    @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Số điện thoại không hợp lệ (VD: 0912345678 hoặc +84912345678)")
    private String phoneNumber; // SĐT khách hàng (cho walk-in customer)
    
    private Long branchId; // Chi nhánh xử lý (nullable)
    
    @NotEmpty(message = "Đơn hàng phải có ít nhất một dịch vụ")
    @Valid
    private List<OrderItemRequest> items; // Danh sách dịch vụ
    
    private String notes; // Ghi chú thêm
    
    private List<String> promotionCodes; // Danh sách mã giảm giá
}
