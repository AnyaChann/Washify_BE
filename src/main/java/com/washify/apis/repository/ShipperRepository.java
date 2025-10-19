package com.washify.apis.repository;

import com.washify.apis.entity.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Shipper entity
 * Cung cấp các phương thức truy vấn database cho bảng shippers
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
     * Tìm shipper theo tên
     * @param name Tên shipper
     * @return Optional chứa Shipper nếu tìm thấy
     */
    Optional<Shipper> findByName(String name);
    
    /**
     * Tìm shipper theo số điện thoại
     * @param phone Số điện thoại
     * @return Optional chứa Shipper nếu tìm thấy
     */
    Optional<Shipper> findByPhone(String phone);
    
    /**
     * Tìm shipper theo biển số xe
     * @param vehicleNumber Biển số xe
     * @return Optional chứa Shipper nếu tìm thấy
     */
    Optional<Shipper> findByVehicleNumber(String vehicleNumber);
}
