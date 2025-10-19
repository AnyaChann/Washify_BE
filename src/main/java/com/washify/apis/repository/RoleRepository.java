package com.washify.apis.repository;

import com.washify.apis.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface cho Role entity
 * Cung cấp các phương thức truy vấn database cho bảng roles
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Tìm role theo tên
     * @param name Tên role (VD: ADMIN, STAFF, CUSTOMER)
     * @return Optional chứa Role nếu tìm thấy
     */
    Optional<Role> findByName(String name);
    
    /**
     * Kiểm tra role có tồn tại theo tên
     * @param name Tên role
     * @return true nếu role tồn tại
     */
    boolean existsByName(String name);
}
