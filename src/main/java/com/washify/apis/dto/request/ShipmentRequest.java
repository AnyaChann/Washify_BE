package com.washify.apis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho request tạo/cập nhật giao hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest {
    
    @NotNull(message = "Order ID không được để trống")
    private Long orderId;
    
    @NotNull(message = "User ID không được để trống")
    private Long userId;
    
    private Long shipperId; // ID shipper (nullable)
    
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    @Size(max = 255, message = "Địa chỉ không quá 255 ký tự")
    private String address;
    
    private LocalDateTime deliveryDate; // Thời gian giao dự kiến
    
    @Size(max = 100, message = "Tên shipper không quá 100 ký tự")
    private String shipperName; // Tên shipper (backup)
    
    @Size(max = 20, message = "SĐT shipper không quá 20 ký tự")
    private String shipperPhone; // SĐT shipper (backup)
}
