package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity lưu token để reset password
 * Token có thời gian hết hạn (thường 15-30 phút)
 */
@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token; // Reset token (UUID hoặc random string)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User yêu cầu reset password
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate; // Thời gian hết hạn (thường +15 phút)
    
    @Column(name = "is_used")
    private Boolean isUsed = false; // Token đã được sử dụng chưa
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * Kiểm tra token có hết hạn chưa
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    /**
     * Kiểm tra token có hợp lệ không (chưa hết hạn và chưa dùng)
     */
    public boolean isValid() {
        return !isExpired() && !isUsed;
    }
}
