package com.washify.apis.service;

import com.washify.apis.entity.PasswordChange2FAToken;
import com.washify.apis.entity.User;
import com.washify.apis.exception.InvalidPasswordResetTokenException;
import com.washify.apis.exception.ResourceNotFoundException;
import com.washify.apis.repository.PasswordChange2FATokenRepository;
import com.washify.apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service xử lý bật/tắt 2FA cho password change với email confirmation
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PasswordChange2FAService {
    
    private final UserRepository userRepository;
    private final PasswordChange2FATokenRepository tokenRepository;
    private final EmailService emailService;
    
    private static final int EXPIRY_MINUTES = 30;
    
    /**
     * Bước 1: Request bật/tắt 2FA
     * Tạo token và gửi email xác nhận
     */
    public void request2FAToggle(Long userId, boolean enable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Check xem đang cùng trạng thái không
        if (Boolean.TRUE.equals(user.getRequireEmailVerificationForPasswordChange()) == enable) {
            String status = enable ? "đã được bật" : "đã được tắt";
            throw new IllegalArgumentException("Bảo mật 2 lớp " + status + " rồi");
        }
        
        // Xóa các token cũ của user
        tokenRepository.deleteByUser(user);
        
        // Tạo token mới
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(EXPIRY_MINUTES);
        
        PasswordChange2FAToken toggle2FAToken = new PasswordChange2FAToken();
        toggle2FAToken.setToken(token);
        toggle2FAToken.setUser(user);
        toggle2FAToken.setEnable2FA(enable);
        toggle2FAToken.setExpiryDate(expiryDate);
        toggle2FAToken.setIsUsed(false);
        
        tokenRepository.save(toggle2FAToken);
        
        // Gửi email xác nhận
        emailService.send2FAToggleConfirmationEmail(user.getEmail(), token, enable);
        
        log.info("2FA toggle request created for user {}: {} 2FA", 
            userId, enable ? "enable" : "disable");
    }
    
    /**
     * Bước 2: Verify token có hợp lệ không
     */
    @Transactional(readOnly = true)
    public boolean validate2FAToggleToken(String token) {
        PasswordChange2FAToken toggle2FAToken = tokenRepository.findByToken(token)
                .orElse(null);
        
        if (toggle2FAToken == null) {
            return false;
        }
        
        return toggle2FAToken.isValid();
    }
    
    /**
     * Bước 3: Confirm bật/tắt 2FA với token
     */
    public void confirm2FAToggle(String token) {
        PasswordChange2FAToken toggle2FAToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidPasswordResetTokenException("Token không hợp lệ"));
        
        if (!toggle2FAToken.isValid()) {
            throw new InvalidPasswordResetTokenException("Token đã hết hạn hoặc đã được sử dụng");
        }
        
        User user = toggle2FAToken.getUser();
        boolean enable = toggle2FAToken.getEnable2FA();
        
        // Apply setting
        user.setRequireEmailVerificationForPasswordChange(enable);
        userRepository.save(user);
        
        // Đánh dấu token đã sử dụng
        toggle2FAToken.setIsUsed(true);
        tokenRepository.save(toggle2FAToken);
        
        // Xóa các token còn lại của user
        tokenRepository.deleteByUser(user);
        
        log.info("2FA {} for user {}", enable ? "enabled" : "disabled", user.getId());
    }
    
    /**
     * Cleanup expired tokens
     */
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        log.info("Expired 2FA toggle tokens cleaned up");
    }
}
