package com.washify.apis.repository;

import com.washify.apis.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface cho Notification entity
 * Cung cấp các phương thức truy vấn database cho bảng notifications
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Tìm tất cả notifications của một user
     * @param userId ID của user
     * @return Danh sách notifications
     */
    List<Notification> findByUserId(Long userId);
    
    /**
     * Tìm notifications chưa đọc của user
     * @param userId ID của user
     * @param isRead Trạng thái đã đọc
     * @return Danh sách notifications chưa đọc
     */
    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);
    
    /**
     * Tìm notifications của user, sắp xếp theo thời gian mới nhất
     * @param userId ID của user
     * @return Danh sách notifications
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Đếm số notifications chưa đọc của user
     * @param userId ID của user
     * @param isRead Trạng thái đã đọc
     * @return Số lượng notifications chưa đọc
     */
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
    
    /**
     * Xóa tất cả notifications của một user
     * @param userId ID của user
     */
    void deleteByUserId(Long userId);
}
