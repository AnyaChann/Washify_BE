package com.washify.apis.repository;

import com.washify.apis.entity.PasswordChange2FAToken;
import com.washify.apis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository cho PasswordChange2FAToken
 */
@Repository
public interface PasswordChange2FATokenRepository extends JpaRepository<PasswordChange2FAToken, Long> {
    
    /**
     * Tìm token theo chuỗi token
     */
    Optional<PasswordChange2FAToken> findByToken(String token);
    
    /**
     * Xóa các token đã hết hạn
     */
    void deleteByExpiryDateBefore(LocalDateTime now);
    
    /**
     * Xóa tất cả token của user
     */
    void deleteByUser(User user);
}
