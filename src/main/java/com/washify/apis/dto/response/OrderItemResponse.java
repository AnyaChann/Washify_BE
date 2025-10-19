package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho response chi tiết item trong đơn hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    
    private Long id;
    private Long serviceId;
    private String serviceName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal; // quantity * price
}
