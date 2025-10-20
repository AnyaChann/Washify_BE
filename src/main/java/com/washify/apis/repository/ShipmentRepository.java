package com.washify.apis.repository;

import com.washify.apis.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Shipment entity
 * Cung cấp các phương thức truy vấn database cho bảng shipments
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    
    /**
     * Tìm shipment theo order ID
     * @param orderId ID của order
     * @return Optional chứa Shipment nếu tìm thấy
     */
    Optional<Shipment> findByOrderId(Long orderId);
    
    /**
     * Tìm shipments theo user ID
     * @param userId ID của user
     * @return Danh sách shipments
     */
    List<Shipment> findByUserId(Long userId);
    
    /**
     * Tìm shipments theo shipper ID
     * @param shipperId ID của shipper
     * @return Danh sách shipments
     */
    List<Shipment> findByShipperId(Long shipperId);
    
    /**
     * Tìm shipments theo trạng thái giao hàng
     * @param deliveryStatus Trạng thái giao hàng
     * @return Danh sách shipments
     */
    List<Shipment> findByDeliveryStatus(Shipment.DeliveryStatus deliveryStatus);
    
    /**
     * Tìm shipments của shipper theo trạng thái
     * @param shipperId ID của shipper
     * @param deliveryStatus Trạng thái giao hàng
     * @return Danh sách shipments
     */
    List<Shipment> findByShipperIdAndDeliveryStatus(Long shipperId, Shipment.DeliveryStatus deliveryStatus);
    
    /**
     * Tìm shipments trong khoảng thời gian giao hàng
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách shipments
     */
    List<Shipment> findByDeliveryDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Đếm số shipments theo trạng thái
     * @param deliveryStatus Trạng thái giao hàng
     * @return Số lượng shipments
     */
    long countByDeliveryStatus(Shipment.DeliveryStatus deliveryStatus);
    
    /**
     * Đếm số shipments của shipper
     * @param shipperId ID của shipper
     * @return Số lượng shipments
     */
    long countByShipperId(Long shipperId);
    
    /**
     * Đếm số shipments của shipper theo trạng thái
     * @param shipperId ID của shipper
     * @param deliveryStatus Trạng thái giao hàng
     * @return Số lượng shipments
     */
    long countByShipperIdAndDeliveryStatus(Long shipperId, Shipment.DeliveryStatus deliveryStatus);
    
    /**
     * Đếm số shipments của shipper có trạng thái nằm trong danh sách
     * @param shipperId ID của shipper
     * @param deliveryStatuses Danh sách trạng thái giao hàng
     * @return Số lượng shipments
     */
    long countByShipperIdAndDeliveryStatusIn(Long shipperId, List<Shipment.DeliveryStatus> deliveryStatuses);
    
    // ========================================
    // PHASE 3: STATISTICS QUERIES
    // ========================================
    
    /**
     * Đếm tổng số shipments (all statuses)
     */
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s) FROM Shipment s")
    long countAllShipments();
    
    /**
     * Tính thời gian giao hàng trung bình (tính từ lúc đặt hàng đến khi giao xong)
     * Chỉ tính cho các shipment đã DELIVERED
     * Sử dụng Order.orderDate làm thời điểm bắt đầu vì Shipment không có pickupDate
     */
    @org.springframework.data.jpa.repository.Query(
        "SELECT AVG(TIMESTAMPDIFF(HOUR, s.order.orderDate, s.deliveryDate)) " +
        "FROM Shipment s " +
        "WHERE s.deliveryStatus = 'DELIVERED' AND s.order.orderDate IS NOT NULL AND s.deliveryDate IS NOT NULL"
    )
    Double getAverageDeliveryTimeInHours();
}
