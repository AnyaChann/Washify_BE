package com.washify.apis.repository;

import com.washify.apis.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Branch entity
 * Cung cấp các phương thức truy vấn database cho bảng branches
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    
    /**
     * Tìm branch theo tên
     * @param name Tên chi nhánh
     * @return Optional chứa Branch nếu tìm thấy
     */
    Optional<Branch> findByName(String name);
    
    /**
     * Tìm các branch theo tên quản lý
     * @param managerName Tên quản lý
     * @return Danh sách các branch
     */
    List<Branch> findByManagerName(String managerName);
    
    /**
     * Tìm branch theo số điện thoại
     * @param phone Số điện thoại chi nhánh
     * @return Optional chứa Branch nếu tìm thấy
     */
    Optional<Branch> findByPhone(String phone);
}
