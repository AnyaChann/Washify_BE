package com.washify.apis.controller;

import com.washify.apis.dto.request.ShipmentRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.ShipmentResponse;
import com.washify.apis.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến Shipment
 */
@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShipmentController {
    
    private final ShipmentService shipmentService;
    
    /**
     * Tạo giao hàng mới
     * POST /api/shipments
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(@Valid @RequestBody ShipmentRequest request) {
        ShipmentResponse shipment = shipmentService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(shipment, "Tạo giao hàng thành công"));
    }
    
    /**
     * Lấy thông tin giao hàng theo ID
     * GET /api/shipments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentById(@PathVariable Long id) {
        ShipmentResponse shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Lấy thông tin giao hàng thành công"));
    }
    
    /**
     * Lấy giao hàng theo order ID
     * GET /api/shipments/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByOrderId(@PathVariable Long orderId) {
        ShipmentResponse shipment = shipmentService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Lấy thông tin giao hàng thành công"));
    }
    
    /**
     * Lấy danh sách giao hàng của shipper
     * GET /api/shipments/shipper/{shipperId}
     */
    @GetMapping("/shipper/{shipperId}")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getShipmentsByShipperId(@PathVariable Long shipperId) {
        List<ShipmentResponse> shipments = shipmentService.getShipmentsByShipperId(shipperId);
        return ResponseEntity.ok(ApiResponse.success(shipments, "Lấy danh sách giao hàng thành công"));
    }
    
    /**
     * Lấy danh sách giao hàng theo trạng thái
     * GET /api/shipments/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getShipmentsByStatus(@PathVariable String status) {
        List<ShipmentResponse> shipments = shipmentService.getShipmentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(shipments, "Lấy danh sách giao hàng thành công"));
    }
    
    /**
     * Cập nhật trạng thái giao hàng
     * PATCH /api/shipments/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ShipmentResponse shipment = shipmentService.updateShipmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Cập nhật trạng thái giao hàng thành công"));
    }
    
    /**
     * Gán shipper cho đơn giao hàng
     * PATCH /api/shipments/{id}/assign-shipper
     */
    @PatchMapping("/{id}/assign-shipper")
    public ResponseEntity<ApiResponse<ShipmentResponse>> assignShipper(
            @PathVariable Long id,
            @RequestParam Long shipperId) {
        ShipmentResponse shipment = shipmentService.assignShipper(id, shipperId);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Gán shipper thành công"));
    }
}
