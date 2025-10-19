package com.washify.apis.repository;

import com.washify.apis.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface cho AuditLog entity
 * Cung cấp các phương thức truy vấn database cho bảng audit_log
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * Tìm audit logs của một user
     * @param userId ID của user
     * @return Danh sách audit logs
     */
    List<AuditLog> findByUserId(Long userId);
    
    /**
     * Tìm audit logs theo loại entity
     * @param entityType Loại entity (VD: Order, User, Payment)
     * @return Danh sách audit logs
     */
    List<AuditLog> findByEntityType(String entityType);
    
    /**
     * Tìm audit logs của một entity cụ thể
     * @param entityType Loại entity
     * @param entityId ID của entity
     * @return Danh sách audit logs
     */
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    /**
     * Tìm audit logs theo action
     * @param action Hành động (CREATE, UPDATE, DELETE)
     * @return Danh sách audit logs
     */
    List<AuditLog> findByAction(String action);
    
    /**
     * Tìm audit logs trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách audit logs
     */
    List<AuditLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Tìm audit logs của user trong khoảng thời gian
     * @param userId ID của user
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách audit logs
     */
    List<AuditLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
