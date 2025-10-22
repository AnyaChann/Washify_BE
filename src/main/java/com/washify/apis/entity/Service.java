package com.washify.apis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho các dịch vụ giặt là
 * Hỗ trợ Soft Delete - không xóa vật lý khỏi database
 */
@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE services SET deleted_at = NOW(), is_active = 0 WHERE id = ?") // Soft delete: set deleted_at và inactive
@Where(clause = "deleted_at IS NULL") // Chỉ query các record chưa bị xóa
public class Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name; // Tên dịch vụ (VD: Giặt khô, Giặt ướt, Là hơi)
    
    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả chi tiết dịch vụ
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Giá dịch vụ (có thể theo kg hoặc item)
    
    @Column(name = "estimated_time")
    private Integer estimatedTime; // Thời gian ước tính (đơn vị: giờ)
    
    @Column(name = "is_active")
    private Boolean isActive = true; // Trạng thái hoạt động của dịch vụ
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at") // Timestamp khi bị xóa (soft delete)
    private LocalDateTime deletedAt;
    
    // One-to-Many: Một service có nhiều order items
    @JsonIgnore // Tránh circular reference Service ↔ OrderItem ↔ Order
    @OneToMany(mappedBy = "service")
    private Set<OrderItem> orderItems = new HashSet<>();
}
