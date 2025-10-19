package com.washify.apis.repository;

import com.washify.apis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface cho User entity
 * Cung cấp các phương thức truy vấn database cho bảng users
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
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
}
