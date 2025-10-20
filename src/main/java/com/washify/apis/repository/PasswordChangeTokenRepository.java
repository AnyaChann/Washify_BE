package com.washify.apis.repository;

import com.washify.apis.entity.PasswordChangeToken;
import com.washify.apis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository cho PasswordChangeToken
 */
@Repository
public interface PasswordChangeTokenRepository extends JpaRepository<PasswordChangeToken, Long> {
    
    /**
     * Tìm token theo chuỗi token
     */
    Optional<PasswordChangeToken> findByToken(String token);
    
    /**
     * Tìm các token hợp lệ của user
     */
    Optional<PasswordChangeToken> findByUserAndIsUsedFalseAndExpiryDateAfter(User user, LocalDateTime now);
    
    /**
     * Xóa các token đã hết hạn
     */
    void deleteByExpiryDateBefore(LocalDateTime now);
    
    /**
     * Xóa tất cả token của user
     */
    void deleteByUser(User user);
}
