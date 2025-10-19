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
 * Entity đại diện cho chi nhánh cửa tiệm giặt là
 * Hỗ trợ Soft Delete - không xóa vật lý khỏi database
 */
@Entity
@Table(name = "branches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE branches SET deleted_at = NOW() WHERE id = ?") // Soft delete
@Where(clause = "deleted_at IS NULL") // Chỉ query các record chưa bị xóa
public class Branch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name; // Tên chi nhánh
    
    @Column(nullable = false, length = 255)
    private String address; // Địa chỉ chi nhánh
    
    @Column(length = 20)
    private String phone; // Số điện thoại chi nhánh
    
    @Column(name = "manager_name", length = 100)
    private String managerName; // Tên quản lý chi nhánh
    
    @Column(name = "is_active")
    private Boolean isActive = true; // Trạng thái hoạt động
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Thời gian tạo chi nhánh
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Thời gian cập nhật
    
    @Column(name = "deleted_at") // Timestamp khi bị xóa (soft delete)
    private LocalDateTime deletedAt;
    
    // One-to-Many: Một branch có nhiều users
    @OneToMany(mappedBy = "branch")
    private Set<User> users = new HashSet<>();
    
    // One-to-Many: Một branch có nhiều orders
    @OneToMany(mappedBy = "branch")
    private Set<Order> orders = new HashSet<>();
}
