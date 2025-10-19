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
     * Tìm tất cả notifications của một user với phân trang
     * @param userId ID của user
     * @param pageable Thông tin phân trang
     * @return Page chứa notifications
     */
    org.springframework.data.domain.Page<Notification> findByUserId(Long userId, org.springframework.data.domain.Pageable pageable);
    
    /**
     * Tìm notifications chưa đọc của user
     * @param userId ID của user
     * @return Danh sách notifications chưa đọc
     */
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    
    /**
     * Tìm notifications của user, sắp xếp theo thời gian mới nhất
     * @param userId ID của user
     * @return Danh sách notifications
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Đếm số notifications chưa đọc của user
     * @param userId ID của user
     * @return Số lượng notifications chưa đọc
     */
    Long countByUserIdAndIsReadFalse(Long userId);
    
    /**
     * Xóa tất cả notifications của một user
     * @param userId ID của user
     */
    void deleteByUserId(Long userId);
}
