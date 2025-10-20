package com.washify.apis.service;

import com.washify.apis.dto.request.UserUpdateRequest;
import com.washify.apis.dto.response.UserResponse;
import com.washify.apis.entity.Branch;
import com.washify.apis.entity.Role;
import com.washify.apis.entity.User;
import com.washify.apis.exception.BadRequestException;
import com.washify.apis.exception.ResourceNotFoundException;
import com.washify.apis.repository.BranchRepository;
import com.washify.apis.repository.RoleRepository;
import com.washify.apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho User
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Lấy thông tin user theo ID
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToUserResponse(user);
    }
    
    /**
     * Lấy User entity theo ID (internal use)
     */
    @Transactional(readOnly = true)
    public User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
    
    /**
     * Lấy thông tin user theo email
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return mapToUserResponse(user);
    }
    
    /**
     * Lấy danh sách tất cả users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật thông tin user
     */
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", request.getBranchId()));
            user.setBranch(branch);
        }
        
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }
    
    /**
     * Xóa user (soft delete)
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        userRepository.deleteById(userId);
    }
    
    /**
     * Gán role cho user
     */
    public UserResponse assignRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
        
        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }
    
    /**
     * Đổi mật khẩu (với tùy chọn email verification)
     * - Nếu user BẬT bảo mật 2 lớp → Gửi email xác nhận
     * - Nếu user TẮT bảo mật 2 lớp → Đổi ngay lập tức
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Check xem user có bật bảo mật 2 lớp không
        if (Boolean.TRUE.equals(user.getRequireEmailVerificationForPasswordChange())) {
            // MODE 1: Bảo mật cao - Gửi email xác nhận
            // Note: PasswordChangeService sẽ tự verify currentPassword
            // và gửi email confirmation
            log.info("User {} has 2FA enabled - sending email verification", userId);
            // Sẽ được gọi từ UserController qua PasswordChangeService
        } else {
            // MODE 2: Nhanh chóng - Đổi ngay lập tức
            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new BadRequestException("Mật khẩu hiện tại không đúng");
            }
            
            // Update password ngay
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            log.info("Password changed instantly for user {} (2FA disabled)", userId);
        }
    }
    
    /**
     * Bật/tắt bảo mật 2 lớp cho việc đổi password
     */
    public void togglePasswordChangeEmailVerification(Long userId, boolean enable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        user.setRequireEmailVerificationForPasswordChange(enable);
        userRepository.save(user);
        
        log.info("User {} {} email verification for password change", 
            userId, enable ? "enabled" : "disabled");
    }
    
    /**
     * Map Entity sang DTO Response
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .requireEmailVerificationForPasswordChange(user.getRequireEmailVerificationForPasswordChange())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .branchId(user.getBranch() != null ? user.getBranch().getId() : null)
                .branchName(user.getBranch() != null ? user.getBranch().getName() : null)
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
