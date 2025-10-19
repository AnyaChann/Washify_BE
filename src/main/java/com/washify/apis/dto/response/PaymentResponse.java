package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho response thông tin thanh toán
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private Long id;
    private Long orderId;
    private String paymentMethod; // CASH, CARD, ONLINE
    private String paymentStatus; // PENDING, PAID, FAILED
    private LocalDateTime paymentDate;
    private BigDecimal amount;
}
