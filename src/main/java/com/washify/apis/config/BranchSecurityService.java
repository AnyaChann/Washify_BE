package com.washify.apis.config;

import com.washify.apis.entity.User;
import com.washify.apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service để kiểm tra quyền truy cập branch-level cho MANAGER
 * MANAGER chỉ có thể cập nhật thông tin chi nhánh mà họ quản lý
 */
@Service("branchSecurity")
@RequiredArgsConstructor
public class BranchSecurityService {
    
    private final UserRepository userRepository;
    
    /**
     * Kiểm tra xem user hiện tại có phải là manager của branch này không
     * 
     * @param branchId ID của branch cần kiểm tra
     * @param authentication Thông tin authentication của user hiện tại
     * @return true nếu user là manager của branch này, false nếu không
     */
    public boolean isBranchManager(Long branchId, Authentication authentication) {
        if (authentication == null || branchId == null) {
            return false;
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return false;
        }
        
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUsername(username)
                .orElse(null);
        
        if (user == null || user.getBranch() == null) {
            return false;
        }
        
        // Kiểm tra user có role MANAGER và branch_id khớp
        boolean isManager = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("MANAGER"));
        
        return isManager && user.getBranch().getId().equals(branchId);
    }
    
    /**
     * Kiểm tra xem user có phải là ADMIN không
     * ADMIN có toàn quyền, không cần kiểm tra branch
     */
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
