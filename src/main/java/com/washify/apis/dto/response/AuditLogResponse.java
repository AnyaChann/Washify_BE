package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO response cho Audit Log
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;
    private String action;
    private String entityType;
    private Long entityId;
    private String username;
    private String ipAddress;
    private String userAgent;
    private String description;
    private String status;
    private String errorMessage;
    private String details; // Combined old/new value details
    private LocalDateTime timestamp;
}
