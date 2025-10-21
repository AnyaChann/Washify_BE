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
    private String orderCode;
    private String paymentMethod; // CASH, MOMO, ZALOPAY, VNPAY, BANK_TRANSFER
    private String paymentStatus; // PENDING, PAID, FAILED
    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private String transactionId; // Mã giao dịch từ payment gateway
    private String paymentUrl; // URL để customer thanh toán (MoMo, VNPay, etc.)
    private String qrCode; // QR code cho thanh toán
}
