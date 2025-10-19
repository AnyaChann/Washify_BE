package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO response cho Shipper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperResponse {

    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String vehicleType;
    private String vehicleNumber;
    private String address;
    private Boolean isActive;
    private Integer totalDeliveries;
    private Integer completedDeliveries;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}
