package com.washify.apis.repository;

import com.washify.apis.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Service entity
 * Cung cấp các phương thức truy vấn database cho bảng services
 * Hỗ trợ Soft Delete
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    /**
     * Tìm tất cả services đang hoạt động
     * @param isActive Trạng thái hoạt động
     * @return Danh sách services đang active
     */
    List<Service> findByIsActive(Boolean isActive);
    
    /**
     * Tìm services theo tên (tìm kiếm không phân biệt hoa thường)
     * @param name Tên service
     * @return Danh sách services có tên chứa từ khóa
     */
    List<Service> findByNameContainingIgnoreCase(String name);
    
    /**
     * Tìm services đang hoạt động và sắp xếp theo giá
     * @param isActive Trạng thái hoạt động
     * @return Danh sách services được sắp xếp theo giá tăng dần
     */
    List<Service> findByIsActiveOrderByPriceAsc(Boolean isActive);
    
    // ========================================
    // SOFT DELETE METHODS
    // ========================================
    
    @Query(value = "SELECT * FROM services WHERE deleted_at IS NOT NULL", nativeQuery = true)
    List<Service> findAllDeleted();
    
    @Query(value = "SELECT * FROM services WHERE id = :id AND deleted_at IS NOT NULL", nativeQuery = true)
    Optional<Service> findDeletedById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE services SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    int restoreById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM services WHERE id = :id", nativeQuery = true)
    int permanentlyDeleteById(@Param("id") Long id);
}
