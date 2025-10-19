package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho response thông tin dịch vụ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer estimatedTime;
    private Boolean isActive;
}
