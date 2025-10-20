package com.washify.apis.service;

import com.washify.apis.dto.request.ShipmentRequest;
import com.washify.apis.dto.response.ShipmentResponse;
import com.washify.apis.entity.Order;
import com.washify.apis.entity.Shipment;
import com.washify.apis.entity.Shipper;
import com.washify.apis.entity.User;
import com.washify.apis.repository.OrderRepository;
import com.washify.apis.repository.ShipmentRepository;
import com.washify.apis.repository.ShipperRepository;
import com.washify.apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Shipment
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentService {
    
    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShipperRepository shipperRepository;
    
    /**
     * Tạo giao hàng mới
     */
    public ShipmentResponse createShipment(ShipmentRequest request) {
        // Tìm order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + request.getOrderId()));
        
        // Kiểm tra order đã có shipment chưa
        if (shipmentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new RuntimeException("Đơn hàng này đã có giao hàng");
        }
        
        // Tìm user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));
        
        // Tạo shipment
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setUser(user);
        shipment.setAddress(request.getAddress());
        shipment.setDeliveryStatus(Shipment.DeliveryStatus.PENDING);
        shipment.setDeliveryDate(request.getDeliveryDate());
        shipment.setShipperName(request.getShipperName());
        shipment.setShipperPhone(request.getShipperPhone());
        
        // Set shipper nếu có
        if (request.getShipperId() != null) {
            Shipper shipper = shipperRepository.findById(request.getShipperId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + request.getShipperId()));
            shipment.setShipper(shipper);
        }
        
        Shipment savedShipment = shipmentRepository.save(shipment);
        return mapToShipmentResponse(savedShipment);
    }
    
    /**
     * Lấy thông tin giao hàng theo ID
     */
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentById(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao hàng với ID: " + shipmentId));
        return mapToShipmentResponse(shipment);
    }
    
    /**
     * Lấy giao hàng theo order ID
     */
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByOrderId(Long orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao hàng cho đơn hàng ID: " + orderId));
        return mapToShipmentResponse(shipment);
    }
    
    /**
     * Lấy danh sách giao hàng của shipper
     */
    @Transactional(readOnly = true)
    public List<ShipmentResponse> getShipmentsByShipperId(Long shipperId) {
        return shipmentRepository.findByShipperId(shipperId).stream()
                .map(this::mapToShipmentResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách giao hàng theo trạng thái
     */
    @Transactional(readOnly = true)
    public List<ShipmentResponse> getShipmentsByStatus(String status) {
        Shipment.DeliveryStatus deliveryStatus = Shipment.DeliveryStatus.valueOf(status);
        return shipmentRepository.findByDeliveryStatus(deliveryStatus).stream()
                .map(this::mapToShipmentResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật trạng thái giao hàng
     */
    public ShipmentResponse updateShipmentStatus(Long shipmentId, String status) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao hàng với ID: " + shipmentId));
        
        shipment.setDeliveryStatus(Shipment.DeliveryStatus.valueOf(status));
        Shipment updatedShipment = shipmentRepository.save(shipment);
        
        // Nếu giao hàng thành công, cập nhật trạng thái order
        if (status.equals("DELIVERED")) {
            Order order = shipment.getOrder();
            order.setStatus(Order.OrderStatus.COMPLETED);
            orderRepository.save(order);
        }
        
        return mapToShipmentResponse(updatedShipment);
    }
    
    /**
     * Gán shipper cho đơn giao hàng
     */
    public ShipmentResponse assignShipper(Long shipmentId, Long shipperId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao hàng với ID: " + shipmentId));
        
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + shipperId));
        
        shipment.setShipper(shipper);
        shipment.setDeliveryStatus(Shipment.DeliveryStatus.SHIPPING);
        
        Shipment updatedShipment = shipmentRepository.save(shipment);
        return mapToShipmentResponse(updatedShipment);
    }
    
    /**
     * Map Entity sang DTO Response
     */
    private ShipmentResponse mapToShipmentResponse(Shipment shipment) {
        return ShipmentResponse.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrder().getId())
                .userId(shipment.getUser().getId())
                .userName(shipment.getUser().getFullName())
                .shipperId(shipment.getShipper() != null ? shipment.getShipper().getId() : null)
                .shipperName(shipment.getShipper() != null ? shipment.getShipper().getName() : shipment.getShipperName())
                .shipperPhone(shipment.getShipper() != null ? shipment.getShipper().getPhone() : shipment.getShipperPhone())
                .address(shipment.getAddress())
                .deliveryStatus(shipment.getDeliveryStatus().name())
                .deliveryDate(shipment.getDeliveryDate())
                .build();
    }
    
    // ========================================
    // ENHANCEMENTS - Phase 3: Statistics & Analytics
    // ========================================
    
    /**
     * Lấy thống kê tổng quan về shipments
     */
    @Transactional(readOnly = true)
    public ShipmentStatistics getShipmentStatistics() {
        Long totalShipments = shipmentRepository.countAllShipments();
        Long pendingCount = shipmentRepository.countByDeliveryStatus(Shipment.DeliveryStatus.PENDING);
        Long inTransitCount = shipmentRepository.countByDeliveryStatus(Shipment.DeliveryStatus.SHIPPING);
        Long deliveredCount = shipmentRepository.countByDeliveryStatus(Shipment.DeliveryStatus.DELIVERED);
        Long cancelledCount = shipmentRepository.countByDeliveryStatus(Shipment.DeliveryStatus.CANCELLED);
        
        // Tính tỷ lệ giao hàng thành công (delivered / (total - cancelled))
        double successRate = 0.0;
        if (totalShipments > 0 && cancelledCount < totalShipments) {
            successRate = (deliveredCount * 100.0) / (totalShipments - cancelledCount);
        }
        
        // Lấy thời gian giao hàng trung bình
        Double averageDeliveryTimeHours = shipmentRepository.getAverageDeliveryTimeInHours();
        if (averageDeliveryTimeHours == null) {
            averageDeliveryTimeHours = 0.0;
        }
        
        return new ShipmentStatistics(
                totalShipments,
                pendingCount,
                inTransitCount,
                deliveredCount,
                cancelledCount,
                successRate,
                averageDeliveryTimeHours
        );
    }
    
    /**
     * Inner class chứa thống kê shipment
     */
    public static class ShipmentStatistics {
        public final Long totalShipments;
        public final Long pendingCount;
        public final Long inTransitCount;
        public final Long deliveredCount;
        public final Long cancelledCount;
        public final Double successRate; // Phần trăm giao hàng thành công
        public final Double averageDeliveryTimeHours; // Thời gian giao hàng trung bình (giờ)
        
        public ShipmentStatistics(Long totalShipments, Long pendingCount, Long inTransitCount,
                                  Long deliveredCount, Long cancelledCount, Double successRate,
                                  Double averageDeliveryTimeHours) {
            this.totalShipments = totalShipments;
            this.pendingCount = pendingCount;
            this.inTransitCount = inTransitCount;
            this.deliveredCount = deliveredCount;
            this.cancelledCount = cancelledCount;
            this.successRate = successRate;
            this.averageDeliveryTimeHours = averageDeliveryTimeHours;
        }
    }
    
    // ========================================
    // ENHANCEMENTS - Phase 2: Attachment Management
    // ========================================
    // Note: File upload implementation is simplified
    // Production should use proper file storage service (S3, Azure Blob, etc.)
}
