package com.washify.apis.repository;

import com.washify.apis.entity.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Shipper entity
 * Cung cấp các phương thức truy vấn database cho bảng shippers
 * Hỗ trợ Soft Delete
 */
@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    
    /**
     * Tìm shippers đang hoạt động
     * @param isActive Trạng thái hoạt động
     * @return Danh sách shippers
     */
    List<Shipper> findByIsActive(Boolean isActive);
    
    /**
     * Tìm tất cả shippers đang hoạt động (isActive = true)
     * @return Danh sách shippers active
     */
    List<Shipper> findByIsActiveTrue();
    
    /**
     * Tìm shipper theo tên
     * @param name Tên shipper
     * @return Optional chứa Shipper nếu tìm thấy
     */
    Optional<Shipper> findByName(String name);
    
    /**
     * Tìm shippers theo tên (tìm kiếm gần đúng, không phân biệt chữ hoa/thường)
     * @param name Tên cần tìm
     * @return Danh sách shippers
     */
    List<Shipper> findByNameContainingIgnoreCase(String name);
    
    /**
     * Tìm shipper theo số điện thoại
     * @param phone Số điện thoại
     * @return Optional chứa Shipper nếu tìm thấy
     */
    Optional<Shipper> findByPhone(String phone);
    
    /**
     * Tìm shipper theo số điện thoại (tìm kiếm gần đúng)
     * @param phone Số điện thoại cần tìm
     * @return Optional chứa Shipper nếu tìm thấy
     */
    Optional<Shipper> findByPhoneContaining(String phone);
    
    /**
     * Tìm shipper theo biển số xe
     * @param vehicleNumber Biển số xe
     * @return Optional chứa Shipper nếu tìm thấy
     */
    Optional<Shipper> findByVehicleNumber(String vehicleNumber);
    
    // ========================================
    // SOFT DELETE METHODS
    // ========================================
    
    /**
     * Lấy tất cả shippers đã bị soft delete (deleted_at NOT NULL AND is_active = 0)
     */
    @Query(value = "SELECT * FROM shippers WHERE deleted_at IS NOT NULL AND is_active = 0", nativeQuery = true)
    List<Shipper> findAllDeleted();
    
    /**
     * Tìm shipper đã bị soft delete theo ID
     */
    @Query(value = "SELECT * FROM shippers WHERE id = :id AND deleted_at IS NOT NULL AND is_active = 0", nativeQuery = true)
    Optional<Shipper> findDeletedById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE shippers SET deleted_at = NULL, is_active = 1 WHERE id = :id", nativeQuery = true)
    int restoreById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM shippers WHERE id = :id", nativeQuery = true)
    int permanentlyDeleteById(@Param("id") Long id);
}
