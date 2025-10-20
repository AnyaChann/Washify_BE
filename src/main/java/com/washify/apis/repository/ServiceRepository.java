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
    // PHASE 3: ADVANCED SEARCH QUERIES
    // ========================================
    
    /**
     * Tìm kiếm services theo nhiều tiêu chí (dynamic query)
     */
    @Query("SELECT s FROM Service s WHERE " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:minPrice IS NULL OR s.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR s.price <= :maxPrice) AND " +
           "(:isActive IS NULL OR s.isActive = :isActive)")
    List<Service> advancedSearch(
        @Param("name") String name,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("isActive") Boolean isActive
    );
    
    /**
     * Tìm services theo khoảng giá
     */
    @Query("SELECT s FROM Service s WHERE s.price >= :minPrice AND s.price <= :maxPrice ORDER BY s.price ASC")
    List<Service> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    // ========================================
    // SOFT DELETE METHODS
    // ========================================
    
    /**
     * Lấy tất cả services đã bị soft delete (deleted_at NOT NULL AND is_active = 0)
     */
    @Query(value = "SELECT * FROM services WHERE deleted_at IS NOT NULL AND is_active = 0", nativeQuery = true)
    List<Service> findAllDeleted();
    
    /**
     * Tìm service đã bị soft delete theo ID
     */
    @Query(value = "SELECT * FROM services WHERE id = :id AND deleted_at IS NOT NULL AND is_active = 0", nativeQuery = true)
    Optional<Service> findDeletedById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE services SET deleted_at = NULL, is_active = 1 WHERE id = :id", nativeQuery = true)
    int restoreById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM services WHERE id = :id", nativeQuery = true)
    int permanentlyDeleteById(@Param("id") Long id);
}
