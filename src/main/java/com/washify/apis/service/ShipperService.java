package com.washify.apis.service;

import com.washify.apis.annotation.Audited;
import com.washify.apis.dto.request.ShipperRequest;
import com.washify.apis.dto.response.ShipperResponse;
import com.washify.apis.entity.Shipper;
import com.washify.apis.entity.Shipment;
import com.washify.apis.repository.ShipmentRepository;
import com.washify.apis.repository.ShipperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Shipper (người giao hàng)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ShipperService {
    
    private final ShipperRepository shipperRepository;
    private final ShipmentRepository shipmentRepository;
    
    /**
     * Tạo shipper mới
     */
    @Audited(action = "CREATE_SHIPPER", entityType = "Shipper", description = "Tạo shipper mới")
    public ShipperResponse createShipper(ShipperRequest request) {
        Shipper shipper = new Shipper();
        shipper.setName(request.getFullName());
        shipper.setPhone(request.getPhoneNumber());
        shipper.setVehicleNumber(request.getVehicleNumber());
        shipper.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        Shipper savedShipper = shipperRepository.save(shipper);
        return mapToShipperResponse(savedShipper);
    }
    
    /**
     * Lấy shipper theo ID
     */
    @Transactional(readOnly = true)
    public ShipperResponse getShipperById(Long id) {
        Shipper shipper = shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + id));
        return mapToShipperResponse(shipper);
    }
    
    /**
     * Lấy tất cả shippers
     */
    @Transactional(readOnly = true)
    public List<ShipperResponse> getAllShippers() {
        return shipperRepository.findAll().stream()
                .map(this::mapToShipperResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy các shippers đang active
     */
    @Transactional(readOnly = true)
    public List<ShipperResponse> getActiveShippers() {
        return shipperRepository.findByIsActiveTrue().stream()
                .map(this::mapToShipperResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Tìm shipper theo số điện thoại
     */
    @Transactional(readOnly = true)
    public List<ShipperResponse> findByPhone(String phone) {
        return shipperRepository.findByPhoneContaining(phone).stream()
                .map(this::mapToShipperResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Tìm shipper theo tên
     */
    @Transactional(readOnly = true)
    public List<ShipperResponse> findByName(String name) {
        return shipperRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToShipperResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật shipper
     */
    @Audited(action = "UPDATE_SHIPPER", entityType = "Shipper", description = "Cập nhật thông tin shipper")
    public ShipperResponse updateShipper(Long id, ShipperRequest request) {
        Shipper shipper = shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + id));
        
        shipper.setName(request.getFullName());
        shipper.setPhone(request.getPhoneNumber());
        shipper.setVehicleNumber(request.getVehicleNumber());
        shipper.setIsActive(request.getIsActive());
        
        Shipper updatedShipper = shipperRepository.save(shipper);
        return mapToShipperResponse(updatedShipper);
    }
    
    /**
     * Xóa shipper (soft delete)
     */
    @Audited(action = "DELETE_SHIPPER", entityType = "Shipper", description = "Xóa mềm shipper")
    public void deleteShipper(Long id) {
        Shipper shipper = shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + id));
        
        // Kiểm tra xem shipper có shipment đang active không
        long activeShipmentCount = shipmentRepository.countByShipperIdAndDeliveryStatusIn(
            id, 
            List.of(Shipment.DeliveryStatus.PENDING, Shipment.DeliveryStatus.SHIPPING)
        );
        
        if (activeShipmentCount > 0) {
            throw new RuntimeException("Không thể xóa shipper vì còn " + activeShipmentCount + " đơn hàng đang giao");
        }
        
        shipperRepository.delete(shipper); // Soft delete
    }
    
    /**
     * Kích hoạt shipper
     */
    @Audited(action = "ACTIVATE_SHIPPER", entityType = "Shipper", description = "Kích hoạt shipper")
    public ShipperResponse activateShipper(Long id) {
        Shipper shipper = shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + id));
        
        shipper.setIsActive(true);
        Shipper updatedShipper = shipperRepository.save(shipper);
        return mapToShipperResponse(updatedShipper);
    }
    
    /**
     * Vô hiệu hóa shipper
     */
    @Audited(action = "DEACTIVATE_SHIPPER", entityType = "Shipper", description = "Vô hiệu hóa shipper")
    public ShipperResponse deactivateShipper(Long id) {
        Shipper shipper = shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + id));
        
        shipper.setIsActive(false);
        Shipper updatedShipper = shipperRepository.save(shipper);
        return mapToShipperResponse(updatedShipper);
    }
    
    /**
     * Lấy thống kê của shipper
     */
    @Transactional(readOnly = true)
    public ShipperStatistics getShipperStatistics(Long id) {
        Shipper shipper = shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + id));
        
        long totalShipments = shipmentRepository.countByShipperId(id);
        long completedShipments = shipmentRepository.countByShipperIdAndDeliveryStatus(id, Shipment.DeliveryStatus.DELIVERED);
        long activeShipments = shipmentRepository.countByShipperIdAndDeliveryStatusIn(
            id, 
            List.of(Shipment.DeliveryStatus.PENDING, Shipment.DeliveryStatus.SHIPPING)
        );
        
        return new ShipperStatistics(
            shipper.getId(),
            shipper.getName(),
            totalShipments,
            completedShipments,
            activeShipments,
            shipper.getIsActive()
        );
    }
    
    /**
     * Map entity sang response DTO
     */
    private ShipperResponse mapToShipperResponse(Shipper shipper) {
        return ShipperResponse.builder()
                .id(shipper.getId())
                .name(shipper.getName())
                .phone(shipper.getPhone())
                .vehicleNumber(shipper.getVehicleNumber())
                .isActive(shipper.getIsActive())
                .createdAt(shipper.getCreatedAt())
                .updatedAt(shipper.getUpdatedAt())
                .deletedAt(shipper.getDeletedAt())
                .build();
    }
    
    /**
     * Inner class cho shipper statistics
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ShipperStatistics {
        private Long shipperId;
        private String shipperName;
        private long totalShipments;
        private long completedShipments;
        private long activeShipments;
        private boolean isActive;
    }
}
