package com.washify.apis.controller;

import com.washify.apis.dto.request.NotificationRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.NotificationResponse;
import com.washify.apis.enums.NotificationType;
import com.washify.apis.service.NotificationService;
import com.washify.apis.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller cho Notification
 */
@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "🔔 Notifications", description = "Quản lý thông báo, đánh dấu đã đọc")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Tạo thông báo mới (Admin/Staff)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Tạo thông báo mới", description = "Tạo thông báo cho user (Admin/Staff only)")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        
        NotificationResponse response = notificationService.createNotification(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<NotificationResponse>builder()
                        .success(true)
                        .message("Tạo thông báo thành công")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Gửi thông báo hàng loạt (Admin only)
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Gửi thông báo hàng loạt", description = "Gửi thông báo cho nhiều user (Admin only)")
    public ResponseEntity<ApiResponse<Void>> sendBulkNotifications(
            @RequestParam List<Long> userIds,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam NotificationType type) {
        
        notificationService.sendBulkNotifications(userIds, title, message, type);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Gửi thông báo hàng loạt thành công")
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * Lấy tất cả thông báo của user hiện tại
     */
    @GetMapping("/my")
    @Operation(summary = "Lấy thông báo của tôi", description = "Lấy tất cả thông báo của user hiện tại")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Get current user from security context
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        
        // TODO: Get userId from username
        // For now, this will need UserService to get user by username
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(1L, pageable);
        
        return ResponseEntity.ok(ApiResponse.<Page<NotificationResponse>>builder()
                .success(true)
                .message("Lấy thông báo thành công")
                .data(notifications)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * Lấy thông báo chưa đọc
     */
    @GetMapping("/unread")
    @Operation(summary = "Lấy thông báo chưa đọc", description = "Lấy tất cả thông báo chưa đọc của user hiện tại")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications() {
        
        // TODO: Get userId from security context
        Long userId = 1L; // Temporary
        
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
        
        return ResponseEntity.ok(ApiResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Lấy thông báo chưa đọc thành công")
                .data(notifications)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * Đánh dấu đã đọc
     */
    @PatchMapping("/{id}/read")
    @Operation(summary = "Đánh dấu đã đọc", description = "Đánh dấu thông báo đã đọc")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long id) {
        
        NotificationResponse response = notificationService.markAsRead(id);
        
        return ResponseEntity.ok(ApiResponse.<NotificationResponse>builder()
                .success(true)
                .message("Đánh dấu đã đọc thành công")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * Đánh dấu tất cả đã đọc
     */
    @PatchMapping("/read-all")
    @Operation(summary = "Đánh dấu tất cả đã đọc", description = "Đánh dấu tất cả thông báo đã đọc")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        
        // TODO: Get userId from security context
        Long userId = 1L; // Temporary
        
        notificationService.markAllAsRead(userId);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Đánh dấu tất cả đã đọc thành công")
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * Xóa thông báo
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thông báo", description = "Xóa thông báo")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        
        notificationService.deleteNotification(id);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa thông báo thành công")
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * Đếm thông báo chưa đọc
     */
    @GetMapping("/unread/count")
    @Operation(summary = "Đếm thông báo chưa đọc", description = "Đếm số thông báo chưa đọc")
    public ResponseEntity<ApiResponse<Long>> countUnread() {
        
        // TODO: Get userId from security context
        Long userId = 1L; // Temporary
        
        Long count = notificationService.countUnread(userId);
        
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .success(true)
                .message("Lấy số thông báo chưa đọc thành công")
                .data(count)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
