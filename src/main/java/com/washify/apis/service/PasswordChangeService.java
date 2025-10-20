package com.washify.apis.service;

import com.washify.apis.dto.request.ChangePasswordRequest;
import com.washify.apis.entity.PasswordChangeToken;
import com.washify.apis.entity.User;
import com.washify.apis.exception.BadRequestException;
import com.washify.apis.exception.InvalidPasswordResetTokenException;
import com.washify.apis.exception.ResourceNotFoundException;
import com.washify.apis.repository.PasswordChangeTokenRepository;
import com.washify.apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service xử lý change password với email verification
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PasswordChangeService {
    
    private final UserRepository userRepository;
    private final PasswordChangeTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    
    private static final int EXPIRY_MINUTES = 30;
    
    /**
     * Bước 1: Request change password
     * Verify current password → Hash new password → Send email
     */
    public void requestPasswordChange(Long userId, ChangePasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu hiện tại không đúng");
        }
        
        // Xóa các token cũ của user
        tokenRepository.deleteByUser(user);
        
        // Tạo token mới
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(EXPIRY_MINUTES);
        
        // Hash new password trước khi lưu
        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());
        
        PasswordChangeToken changeToken = new PasswordChangeToken();
        changeToken.setToken(token);
        changeToken.setUser(user);
        changeToken.setNewPasswordHash(newPasswordHash);
        changeToken.setExpiryDate(expiryDate);
        changeToken.setIsUsed(false);
        
        tokenRepository.save(changeToken);
        
        // Gửi email xác nhận
        emailService.sendPasswordChangeConfirmationEmail(user.getEmail(), token);
        
        log.info("Password change request created for user: {}", userId);
    }
    
    /**
     * Bước 2: Verify token có hợp lệ không
     */
    @Transactional(readOnly = true)
    public boolean validatePasswordChangeToken(String token) {
        PasswordChangeToken changeToken = tokenRepository.findByToken(token)
                .orElse(null);
        
        if (changeToken == null) {
            return false;
        }
        
        return changeToken.isValid();
    }
    
    /**
     * Bước 3: Confirm password change với token
     * Apply new password đã lưu trong token
     */
    public void confirmPasswordChange(String token) {
        PasswordChangeToken changeToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidPasswordResetTokenException("Token không hợp lệ"));
        
        if (!changeToken.isValid()) {
            throw new InvalidPasswordResetTokenException("Token đã hết hạn hoặc đã được sử dụng");
        }
        
        User user = changeToken.getUser();
        
        // Apply new password (đã hash sẵn trong token)
        user.setPassword(changeToken.getNewPasswordHash());
        userRepository.save(user);
        
        // Đánh dấu token đã sử dụng
        changeToken.setIsUsed(true);
        tokenRepository.save(changeToken);
        
        // Xóa các token còn lại của user
        tokenRepository.deleteByUser(user);
        
        log.info("Password changed successfully for user: {}", user.getId());
    }
    
    /**
     * Cleanup expired tokens
     */
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        log.info("Expired password change tokens cleaned up");
    }
}
