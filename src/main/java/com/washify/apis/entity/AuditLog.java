package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho nhật ký hoạt động (audit trail)
 * Ghi lại mọi thay đổi dữ liệu quan trọng trong hệ thống
 */
@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many-to-One: Nhiều audit logs của một user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Người thực hiện hành động (nullable nếu là hệ thống)
    
    @Column(name = "entity_type", length = 50)
    private String entityType; // Loại entity (VD: Order, User, Payment)
    
    @Column(name = "entity_id")
    private Long entityId; // ID của entity bị thay đổi
    
    @Column(length = 50)
    private String action; // Hành động (CREATE, UPDATE, DELETE)
    
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue; // Giá trị cũ (dạng JSON)
    
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue; // Giá trị mới (dạng JSON)
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Thời gian thực hiện
}
