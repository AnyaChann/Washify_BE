package com.washify.apis.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO request cho Shipper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperRequest {

    @NotBlank(message = "Tên shipper không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(\\+84|0)[35789]\\d{8}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String vehicleType; // Xe máy, xe tải, ...

    private String vehicleNumber; // Biển số xe

    private String address;

    private Boolean isActive;
}
