package com.washify.apis.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Request cho việc upload attachment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentRequest {
    
    /**
     * ID của order (nullable - chỉ cần một trong orderId hoặc shipmentId)
     */
    private Long orderId;
    
    /**
     * ID của shipment (nullable - chỉ cần một trong orderId hoặc shipmentId)
     */
    private Long shipmentId;
    
    /**
     * URL của file đã upload (có thể từ service upload khác)
     */
    @NotNull(message = "File URL không được để trống")
    private String fileUrl;
    
    /**
     * Loại file (MIME type)
     */
    private String fileType;
}
