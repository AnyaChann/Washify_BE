package com.washify.apis.repository;

import com.washify.apis.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Branch entity
 * Cung cấp các phương thức truy vấn database cho bảng branches
 * Hỗ trợ Soft Delete
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
    
    /**
     * Tìm các branch đang hoạt động
     */
    @Query("SELECT b FROM Branch b WHERE b.isActive = true")
    List<Branch> findAllActive();
    
    // ========================================
    // SOFT DELETE METHODS
    // ========================================
    
    @Query(value = "SELECT * FROM branches WHERE deleted_at IS NOT NULL", nativeQuery = true)
    List<Branch> findAllDeleted();
    
    @Query(value = "SELECT * FROM branches WHERE id = :id AND deleted_at IS NOT NULL", nativeQuery = true)
    Optional<Branch> findDeletedById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE branches SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    int restoreById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM branches WHERE id = :id", nativeQuery = true)
    int permanentlyDeleteById(@Param("id") Long id);
}
