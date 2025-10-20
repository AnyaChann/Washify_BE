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
    private String name; // Match với Shipper.name
    private String phone; // Match với Shipper.phone
    private String vehicleNumber; // Match với Shipper.vehicleNumber
    private Boolean isActive;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private java.time.LocalDateTime deletedAt;
}
