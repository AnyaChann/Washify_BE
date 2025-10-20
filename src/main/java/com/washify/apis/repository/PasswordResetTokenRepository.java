package com.washify.apis.repository;

import com.washify.apis.entity.PasswordResetToken;
import com.washify.apis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository cho PasswordResetToken
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Tìm token theo string value
     */
    Optional<PasswordResetToken> findByToken(String token);
    
    /**
     * Tìm token mới nhất của user (chưa dùng, chưa hết hạn)
     */
    Optional<PasswordResetToken> findByUserAndIsUsedFalseAndExpiryDateAfter(
            User user, 
            LocalDateTime now
    );
    
    /**
     * Xóa các token đã hết hạn (cleanup job)
     */
    void deleteByExpiryDateBefore(LocalDateTime now);
    
    /**
     * Xóa tất cả token của user (khi reset thành công)
     */
    void deleteByUser(User user);
}
