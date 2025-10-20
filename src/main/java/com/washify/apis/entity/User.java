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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho người dùng trong hệ thống (khách hàng, nhân viên, admin)
 * Hỗ trợ Soft Delete - không xóa vật lý khỏi database
 */
@Entity // Đánh dấu đây là JPA entity
@Table(name = "users") // Map với bảng "users"
@Data // Lombok: tự động tạo getters, setters, toString, equals, hashCode
@NoArgsConstructor // Constructor không tham số
@AllArgsConstructor // Constructor đầy đủ tham số
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW(), is_active = 0 WHERE id = ?") // Soft delete: set deleted_at và inactive
@Where(clause = "deleted_at IS NULL") // Chỉ query các record chưa bị xóa
public class User {
    
    @Id // Khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment
    private Long id;
    
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName; // Họ tên đầy đủ
    
    @Column(nullable = false, unique = true, length = 50)
    private String username; // Username (unique, dùng để đăng nhập)
    
    @Column(nullable = false, unique = true, length = 100)
    private String email; // Email (unique)
    
    @Column(nullable = false)
    private String password; // Mật khẩu đã mã hóa
    
    @Column(length = 20)
    private String phone; // Số điện thoại
    
    @Column(length = 255)
    private String address; // Địa chỉ
    
    @Column(name = "is_active")
    private Boolean isActive = true; // Trạng thái hoạt động
    
    @CreationTimestamp // Tự động set thời gian tạo
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp // Tự động update mỗi khi record thay đổi
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at") // Timestamp khi bị xóa (soft delete)
    private LocalDateTime deletedAt;
    
    // Many-to-One: Nhiều users thuộc một branch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch; // Chi nhánh làm việc (nullable)
    
    // Many-to-Many: User có nhiều roles, Role có nhiều users
    @ManyToMany(fetch = FetchType.EAGER) // EAGER: load roles ngay khi load user
    @JoinTable(
        name = "user_roles", // Bảng trung gian
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    // One-to-Many: Một user có nhiều orders
    @JsonIgnore // Tránh circular reference khi serialize JSON
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Order> orders = new HashSet<>();
    
    // One-to-Many: Một user có nhiều reviews
    @JsonIgnore // Tránh circular reference khi serialize JSON
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
    
    // One-to-Many: Một user có nhiều notifications
    @JsonIgnore // Tránh circular reference khi serialize JSON
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Notification> notifications = new HashSet<>();
}
