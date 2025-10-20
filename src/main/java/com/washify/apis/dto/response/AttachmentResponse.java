package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO Response cho Attachment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponse {
    
    private Long id;
    private Long orderId;
    private Long shipmentId;
    private String fileUrl;
    private String fileType;
    private LocalDateTime uploadedAt;
}
