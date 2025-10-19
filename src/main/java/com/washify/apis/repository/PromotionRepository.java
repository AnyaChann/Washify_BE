package com.washify.apis.repository;

import com.washify.apis.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Promotion entity
 * Cung cấp các phương thức truy vấn database cho bảng promotions
 * Hỗ trợ Soft Delete
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    /**
     * Tìm promotion theo mã code
     * @param code Mã promotion
     * @return Optional chứa Promotion nếu tìm thấy
     */
    Optional<Promotion> findByCode(String code);
    
    /**
     * Tìm promotions đang hoạt động
     * @param isActive Trạng thái hoạt động
     * @return Danh sách promotions
     */
    List<Promotion> findByIsActive(Boolean isActive);
    
    /**
     * Tìm promotions theo loại giảm giá
     * @param discountType Loại giảm giá (PERCENT/FIXED)
     * @return Danh sách promotions
     */
    List<Promotion> findByDiscountType(Promotion.DiscountType discountType);
    
    /**
     * Tìm promotions còn hiệu lực (đang trong thời gian áp dụng)
     * @param now Thời điểm hiện tại
     * @return Danh sách promotions
     */
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findValidPromotions(@Param("now") LocalDateTime now);
    
    /**
     * Kiểm tra promotion code có tồn tại không
     * @param code Mã promotion
     * @return true nếu code đã tồn tại
     */
    boolean existsByCode(String code);
    
    /**
     * Tìm promotions sắp hết hạn (trong vòng N ngày)
     * @param now Thời điểm hiện tại
     * @param expiryDate Ngày hết hạn
     * @return Danh sách promotions
     */
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.endDate BETWEEN :now AND :expiryDate")
    List<Promotion> findExpiringPromotions(@Param("now") LocalDateTime now, @Param("expiryDate") LocalDateTime expiryDate);
    
    // ========================================
    // SOFT DELETE METHODS
    // ========================================
    
    @Query(value = "SELECT * FROM promotions WHERE deleted_at IS NOT NULL", nativeQuery = true)
    List<Promotion> findAllDeleted();
    
    @Query(value = "SELECT * FROM promotions WHERE id = :id AND deleted_at IS NOT NULL", nativeQuery = true)
    Optional<Promotion> findDeletedById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE promotions SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    int restoreById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM promotions WHERE id = :id", nativeQuery = true)
    int permanentlyDeleteById(@Param("id") Long id);
}
