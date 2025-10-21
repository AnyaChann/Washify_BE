package com.washify.apis.service;

import com.washify.apis.entity.Role;
import com.washify.apis.entity.User;
import com.washify.apis.repository.RoleRepository;
import com.washify.apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service quản lý Guest User (khách vãng lai)
 * Auto-create guest user khi STAFF tạo order với SĐT mới
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuestUserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${guest.default-password}")
    private String defaultGuestPassword;
    
    /**
     * Tìm hoặc tạo user từ số điện thoại
     * - Nếu SĐT đã tồn tại → Trả về user đó
     * - Nếu SĐT mới → Tạo GUEST user mới
     */
    @Transactional
    public User findOrCreateUserByPhone(String phoneNumber) {
        // Chuẩn hóa SĐT (bỏ +84, thay bằng 0)
        String normalizedPhone = normalizePhoneNumber(phoneNumber);
        
        // Tìm user theo SĐT
        Optional<User> existingUser = userRepository.findByPhone(normalizedPhone);
        
        if (existingUser.isPresent()) {
            log.info("Found existing user with phone: {}", normalizedPhone);
            return existingUser.get();
        }
        
        // Tạo GUEST user mới
        log.info("Creating new GUEST user for phone: {}", normalizedPhone);
        return createGuestUser(normalizedPhone);
    }
    
    /**
     * Tạo GUEST user mới
     */
    private User createGuestUser(String phoneNumber) {
        Role guestRole = roleRepository.findByName("GUEST")
                .orElseThrow(() -> new RuntimeException("GUEST role not found"));
        
        User guestUser = new User();
        guestUser.setPhone(phoneNumber);
        guestUser.setUsername(generateGuestUsername(phoneNumber));
        guestUser.setPassword(passwordEncoder.encode(defaultGuestPassword));
        guestUser.setFullName("Guest-" + phoneNumber);
        guestUser.setEmail(generateGuestEmail(phoneNumber));
        guestUser.setIsActive(true);
        guestUser.setRoles(Set.of(guestRole));
        
        return userRepository.save(guestUser);
    }
    
    /**
     * Upgrade GUEST user thành CUSTOMER
     * Được gọi khi user cập nhật đầy đủ thông tin
     */
    @Transactional
    public void upgradeGuestToCustomer(User user) {
        // Kiểm tra xem có phải GUEST không
        boolean isGuest = user.getRoles().stream()
                .anyMatch(role -> "GUEST".equals(role.getName()));
        
        if (!isGuest) {
            log.info("User {} is not a GUEST, skip upgrade", user.getId());
            return;
        }
        
        // Kiểm tra thông tin đã đầy đủ chưa
        if (!isProfileComplete(user)) {
            log.info("User {} profile incomplete, cannot upgrade to CUSTOMER", user.getId());
            return;
        }
        
        // Remove GUEST role
        user.getRoles().removeIf(role -> "GUEST".equals(role.getName()));
        
        // Add CUSTOMER role
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));
        user.getRoles().add(customerRole);
        
        userRepository.save(user);
        log.info("Successfully upgraded user {} from GUEST to CUSTOMER", user.getId());
    }
    
    /**
     * Kiểm tra thông tin user đã đầy đủ chưa
     */
    private boolean isProfileComplete(User user) {
        return user.getFullName() != null && !user.getFullName().startsWith("Guest-")
                && user.getEmail() != null && !user.getEmail().endsWith("@guest.washify.com")
                && user.getAddress() != null && !user.getAddress().isEmpty();
    }
    
    /**
     * Chuẩn hóa số điện thoại
     * +84912345678 → 0912345678
     */
    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+84")) {
            return "0" + phoneNumber.substring(3);
        }
        return phoneNumber;
    }
    
    /**
     * Tạo username cho guest
     */
    private String generateGuestUsername(String phoneNumber) {
        return "guest_" + phoneNumber;
    }
    
    /**
     * Tạo email tạm cho guest
     */
    private String generateGuestEmail(String phoneNumber) {
        return phoneNumber + "@guest.washify.com";
    }
}
