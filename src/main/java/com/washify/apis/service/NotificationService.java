package com.washify.apis.service;

import com.washify.apis.dto.request.NotificationRequest;
import com.washify.apis.dto.response.NotificationResponse;
import com.washify.apis.entity.Notification;
import com.washify.apis.entity.User;
import com.washify.apis.enums.NotificationType;
import com.washify.apis.exception.ResourceNotFoundException;
import com.washify.apis.repository.NotificationRepository;
import com.washify.apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service cho Notification
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Tạo thông báo mới
     */
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        log.info("Creating notification for user: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType().name()); // Convert enum to String
        notification.setRelatedId(request.getRelatedId());
        notification.setIsRead(false);

        notification = notificationRepository.save(notification);

        return mapToResponse(notification);
    }

    /**
     * Gửi thông báo cho nhiều user
     */
    @Transactional
    public void sendBulkNotifications(List<Long> userIds, String title, String message, NotificationType type) {
        log.info("Sending bulk notifications to {} users", userIds.size());

        List<User> users = userRepository.findAllById(userIds);
        
        List<Notification> notifications = users.stream()
                .map(user -> {
                    Notification notification = new Notification();
                    notification.setUser(user);
                    notification.setTitle(title);
                    notification.setMessage(message);
                    notification.setType(type.name()); // Convert enum to String
                    notification.setIsRead(false);
                    return notification;
                })
                .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
    }

    /**
     * Lấy tất cả thông báo của user
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        log.info("Getting notifications for user: {}", userId);

        return notificationRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Lấy thông báo chưa đọc của user
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        log.info("Getting unread notifications for user: {}", userId);

        return notificationRepository.findByUserIdAndIsReadFalse(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Đánh dấu đã đọc
     */
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);

        return mapToResponse(notification);
    }

    /**
     * Đánh dấu tất cả đã đọc
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);

        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        
        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Xóa thông báo
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        log.info("Deleting notification: {}", notificationId);

        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }

        notificationRepository.deleteById(notificationId);
    }

    /**
     * Đếm thông báo chưa đọc
     */
    @Transactional(readOnly = true)
    public Long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Map Entity to Response
     */
    private NotificationResponse mapToResponse(Notification notification) {
        NotificationResponse response = modelMapper.map(notification, NotificationResponse.class);
        response.setUserId(notification.getUser().getId());
        response.setUsername(notification.getUser().getUsername());
        return response;
    }
}
