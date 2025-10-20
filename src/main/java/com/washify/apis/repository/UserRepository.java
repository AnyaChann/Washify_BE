package com.washify.apis.repository;

import com.washify.apis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho User entity
 * Cung cấp các phương thức truy vấn database cho bảng users
 * Hỗ trợ Soft Delete với các phương thức restore và query deleted records
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Tìm user theo username
     * @param username Username của user
     * @return Optional chứa User nếu tìm thấy
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Kiểm tra username đã tồn tại chưa
     * @param username Username cần kiểm tra
     * @return true nếu username đã tồn tại
     */
    Boolean existsByUsername(String username);
    
    /**
     * Tìm user theo email
     * @param email Email của user
     * @return Optional chứa User nếu tìm thấy
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Kiểm tra email đã tồn tại chưa
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại
     */
    boolean existsByEmail(String email);
    
    /**
     * Tìm user theo số điện thoại
     * @param phone Số điện thoại
     * @return Optional chứa User nếu tìm thấy
     */
    Optional<User> findByPhone(String phone);
    
    // ========================================
    // SOFT DELETE METHODS
    // ========================================
    
    /**
     * Lấy tất cả users đã bị xóa mềm (deleted_at NOT NULL AND is_active = 0)
     */
    @Query(value = "SELECT * FROM users WHERE deleted_at IS NOT NULL AND is_active = 0", nativeQuery = true)
    List<User> findAllDeleted();
    
    /**
     * Tìm user đã bị xóa theo ID (deleted_at NOT NULL AND is_active = 0)
     */
    @Query(value = "SELECT * FROM users WHERE id = :id AND deleted_at IS NOT NULL AND is_active = 0", nativeQuery = true)
    Optional<User> findDeletedById(@Param("id") Long id);
    
    /**
     * Khôi phục user đã bị xóa mềm
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET deleted_at = NULL, is_active = 1 WHERE id = :id", nativeQuery = true)
    int restoreById(@Param("id") Long id);
    
    /**
     * Xóa vĩnh viễn user (hard delete) - CẢNH BÁO: Không thể khôi phục!
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users WHERE id = :id", nativeQuery = true)
    int permanentlyDeleteById(@Param("id") Long id);
    
    /**
     * Tìm tất cả users đang hoạt động (is_active = true)
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActive();
    
    /**
     * Tìm user theo email (bao gồm cả đã xóa)
     */
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmailIncludingDeleted(@Param("email") String email);
    
    // ========================================
    // PHASE 3: ADVANCED SEARCH QUERIES
    // ========================================
    
    /**
     * Tìm kiếm users theo nhiều tiêu chí với JOIN roles
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.roles r WHERE " +
           "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:fullName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
           "(:roleId IS NULL OR r.id = :roleId)")
    List<User> searchUsers(
        @Param("username") String username,
        @Param("email") String email,
        @Param("fullName") String fullName,
        @Param("roleId") Long roleId
    );
    
    /**
     * Lấy users theo role (JOIN với roles Set)
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<User> findByRoleId(@Param("roleId") Long roleId);
}
