package com.washify.apis.dto.request;

import com.washify.apis.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO request cho Notification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String message;

    @NotNull(message = "Loại thông báo không được để trống")
    private NotificationType type;

    private Long relatedId; // ID liên quan (order ID, payment ID, etc.)
}
