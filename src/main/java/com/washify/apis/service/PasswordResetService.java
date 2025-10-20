package com.washify.apis.service;

import com.washify.apis.entity.PasswordResetToken;
import com.washify.apis.entity.User;
import com.washify.apis.exception.InvalidPasswordResetTokenException;
import com.washify.apis.exception.ResourceNotFoundException;
import com.washify.apis.repository.PasswordResetTokenRepository;
import com.washify.apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service xử lý reset password qua email
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PasswordResetService {
    
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    
    private static final int EXPIRY_MINUTES = 30; // Token hết hạn sau 30 phút
    
    /**
     * Bước 1: User request forgot password
     * Tạo token và gửi email với link reset
     */
    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        // Xóa các token cũ của user này (nếu có)
        tokenRepository.deleteByUser(user);
        
        // Tạo token mới
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(EXPIRY_MINUTES);
        
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiryDate);
        resetToken.setIsUsed(false);
        
        tokenRepository.save(resetToken);
        
        // Gửi email với link reset password
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }
    
    /**
     * Bước 2: Verify token có hợp lệ không
     */
    public boolean validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElse(null);
        
        if (resetToken == null) {
            return false;
        }
        
        return resetToken.isValid();
    }
    
    /**
     * Bước 3: Reset password với token
     */
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidPasswordResetTokenException("Token không hợp lệ"));
        
        if (!resetToken.isValid()) {
            throw new InvalidPasswordResetTokenException("Token đã hết hạn hoặc đã được sử dụng");
        }
        
        User user = resetToken.getUser();
        
        // Mã hóa password trước khi lưu
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Đánh dấu token đã sử dụng
        resetToken.setIsUsed(true);
        tokenRepository.save(resetToken);
        
        // Xóa các token còn lại của user
        tokenRepository.deleteByUser(user);
    }
    
    /**
     * Cleanup job: Xóa các token đã hết hạn
     * Có thể chạy định kỳ bằng @Scheduled
     */
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
