package com.washify.apis.repository;

import com.washify.apis.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho entity UserRole (join table giữa User và Role)
 * 
 * Note: Hiện tại dự án đang dùng @JoinTable trong User.java
 * Repository này dành cho trường hợp cần query trực tiếp bảng user_roles
 * hoặc khi mở rộng thêm thuộc tính vào join table
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {
    
    /**
     * Tìm tất cả user roles theo user ID
     * @param userId ID của user
     * @return Danh sách UserRole của user đó
     */
    List<UserRole> findByUserId(Long userId);
    
    /**
     * Tìm tất cả user roles theo role ID
     * @param roleId ID của role
     * @return Danh sách UserRole có role đó
     */
    List<UserRole> findByRoleId(Long roleId);
    
    /**
     * Đếm số user có role cụ thể
     * @param roleId ID của role
     * @return Số lượng user có role này
     */
    long countByRoleId(Long roleId);
    
    /**
     * Kiểm tra xem user có role cụ thể không
     * @param userId ID của user
     * @param roleId ID của role
     * @return true nếu user có role này
     */
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
    
    /**
     * Xóa user role theo user ID và role ID
     * @param userId ID của user
     * @param roleId ID của role
     */
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
    
    /**
     * Xóa tất cả user roles của một user
     * @param userId ID của user
     */
    void deleteByUserId(Long userId);
}
