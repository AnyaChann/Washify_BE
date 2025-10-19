package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho response thông tin giao hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    
    private Long id;
    private Long orderId;
    private Long userId;
    private String userName;
    private Long shipperId;
    private String shipperName;
    private String shipperPhone;
    private String address;
    private String deliveryStatus; // PENDING, SHIPPING, DELIVERED, CANCELLED
    private LocalDateTime deliveryDate;
}
