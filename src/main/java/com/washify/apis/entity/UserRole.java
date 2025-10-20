package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Entity join table cho quan hệ Many-to-Many giữa User và Role
 * Có thể mở rộng thêm thuộc tính như assignedAt, assignedBy, expiryDate, etc.
 * 
 * Note: Hiện tại đang dùng @JoinTable trong User.java
 * File này để sẵn cho trường hợp cần thêm thuộc tính vào join table
 */
@Entity
@Table(name = "user_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class UserRole implements Serializable {
    
    /**
     * Composite Primary Key cho bảng user_roles
     */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UserRoleId implements Serializable {
        
        @Column(name = "user_id")
        private Long userId;
        
        @Column(name = "role_id")
        private Long roleId;
    }
    
    @EmbeddedId
    @EqualsAndHashCode.Include
    private UserRoleId id;
    
    // Many-to-One: Nhiều user_roles thuộc một user
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Map với userId trong composite key
    @JoinColumn(name = "user_id")
    private User user;
    
    // Many-to-One: Nhiều user_roles thuộc một role
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId") // Map với roleId trong composite key
    @JoinColumn(name = "role_id")
    private Role role;
    
    // TODO: Có thể thêm các thuộc tính mở rộng ở đây
    // @CreationTimestamp
    // @Column(name = "assigned_at")
    // private LocalDateTime assignedAt; // Thời gian gán role
    
    // @ManyToOne
    // @JoinColumn(name = "assigned_by")
    // private User assignedBy; // Ai đã gán role này
    
    // @Column(name = "expiry_date")
    // private LocalDateTime expiryDate; // Thời hạn của role (optional)
    
    /**
     * Constructor tiện lợi để tạo UserRole từ User và Role
     */
    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
        this.id = new UserRoleId(user.getId(), role.getId());
    }
}
