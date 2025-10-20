package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity lưu yêu cầu đổi password qua email verification
 * Khác với PasswordResetToken (dùng khi quên password)
 * Entity này lưu cả newPassword để apply sau khi verify email
 */
@Entity
@Table(name = "password_change_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token; // UUID token
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String newPasswordHash; // Password mới đã hash
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate; // Thời gian hết hạn
    
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false; // Token đã được sử dụng chưa
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * Check token đã hết hạn chưa
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    /**
     * Check token có hợp lệ không
     */
    public boolean isValid() {
        return !isExpired() && !isUsed;
    }
}
