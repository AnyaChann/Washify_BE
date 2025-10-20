package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho người giao hàng (shipper nội bộ hoặc đối tác)
 * Hỗ trợ Soft Delete - không xóa vật lý khỏi database
 */
@Entity
@Table(name = "shippers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE shippers SET deleted_at = NOW(), is_active = 0 WHERE id = ?") // Soft delete: set deleted_at và inactive
@Where(clause = "deleted_at IS NULL") // Chỉ query các record chưa bị xóa
public class Shipper {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name; // Tên shipper
    
    @Column(length = 20)
    private String phone; // Số điện thoại liên hệ
    
    @Column(name = "vehicle_number", length = 50)
    private String vehicleNumber; // Biển số xe
    
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
    
    // One-to-Many: Một shipper có nhiều shipments
    @OneToMany(mappedBy = "shipper")
    private Set<Shipment> shipments = new HashSet<>();
}
