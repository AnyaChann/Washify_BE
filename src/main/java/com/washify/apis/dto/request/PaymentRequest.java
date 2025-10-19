package com.washify.apis.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho request tạo/cập nhật thanh toán
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotNull(message = "Order ID không được để trống")
    private Long orderId;
    
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod; // CASH, CARD, ONLINE
    
    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;
}
