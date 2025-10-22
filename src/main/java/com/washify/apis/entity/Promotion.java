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
 * Entity đại diện cho mã giảm giá/khuyến mãi
 * Hỗ trợ Soft Delete - không xóa vật lý khỏi database
 */
@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE promotions SET deleted_at = NOW(), is_active = 0 WHERE id = ?") // Soft delete: set deleted_at và inactive
@Where(clause = "deleted_at IS NULL") // Chỉ query các record chưa bị xóa
public class Promotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code; // Mã khuyến mãi (VD: SUMMER2025, NEWUSER)
    
    @Column(length = 255)
    private String description; // Mô tả chương trình
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType; // Loại giảm giá (% hoặc cố định)
    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue; // Giá trị giảm (VD: 20% hoặc 50000đ)
    
    @Column(name = "start_date")
    private LocalDateTime startDate; // Thời gian bắt đầu
    
    @Column(name = "end_date")
    private LocalDateTime endDate; // Thời gian kết thúc
    
    @Column(name = "is_active")
    private Boolean isActive = true; // Trạng thái hoạt động
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at") // Timestamp khi bị xóa (soft delete)
    private LocalDateTime deletedAt;
    
    // Many-to-Many với Order (phía bị map)
    @JsonIgnore // Tránh circular reference Promotion ↔ Order
    @ManyToMany(mappedBy = "promotions")
    private Set<Order> orders = new HashSet<>();
    
    /**
     * Enum định nghĩa loại giảm giá
     */
    public enum DiscountType {
        PERCENT, // Giảm theo phần trăm (VD: 20%)
        FIXED    // Giảm giá cố định (VD: 50000đ)
    }
}
