package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho response thông tin đơn hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long id;
    private String orderCode; // Mã đơn hàng (VD: WF202510210001)
    private Long userId;
    private String userName;
    private Long branchId;
    private String branchName;
    private LocalDateTime orderDate;
    private String status; // PENDING, CONFIRMED, PROCESSING, READY, DELIVERING, COMPLETED, CANCELLED, REFUNDED
    private BigDecimal totalAmount;
    private String notes;
    private List<OrderItemResponse> items;
    private PaymentResponse payment;
    private ShipmentResponse shipment;
    private List<String> promotionCodes;
}
