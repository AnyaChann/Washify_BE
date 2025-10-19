package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho response thông tin chi nhánh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse {
    
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String managerName;
    private LocalDateTime createdAt;
}
