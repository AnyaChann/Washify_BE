package com.washify.apis.repository;

import com.washify.apis.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface cho OrderItem entity
 * Cung cấp các phương thức truy vấn database cho bảng order_items
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Tìm tất cả order items của một order
     * @param orderId ID của order
     * @return Danh sách order items
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * Tìm order items theo service ID
     * @param serviceId ID của service
     * @return Danh sách order items
     */
    List<OrderItem> findByServiceId(Long serviceId);
    
    /**
     * Xóa tất cả order items của một order
     * @param orderId ID của order
     */
    void deleteByOrderId(Long orderId);
}
