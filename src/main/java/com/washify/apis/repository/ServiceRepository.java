package com.washify.apis.repository;

import com.washify.apis.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface cho Service entity
 * Cung cấp các phương thức truy vấn database cho bảng services
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
}
