package com.washify.apis.controller;

import com.washify.apis.dto.request.ShipmentRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.ShipmentResponse;
import com.washify.apis.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến Shipment
 */
@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "🚚 Shipments", description = "Quản lý vận chuyển - 🚚 Shipper/Staff/Admin")
public class ShipmentController {
    
    private final ShipmentService shipmentService;
    
    /**
     * Tạo giao hàng mới
     * POST /api/shipments
     * Chỉ Staff và Admin
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(@Valid @RequestBody ShipmentRequest request) {
        ShipmentResponse shipment = shipmentService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(shipment, "Tạo giao hàng thành công"));
    }
    
    /**
     * Lấy thông tin giao hàng theo ID
     * GET /api/shipments/{id}
     * Staff/Admin xem tất cả, Shipper xem của mình
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SHIPPER')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentById(@PathVariable Long id) {
        ShipmentResponse shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Lấy thông tin giao hàng thành công"));
    }
    
    /**
     * Lấy giao hàng theo order ID
     * GET /api/shipments/order/{orderId}
     * Staff/Admin/Shipper có thể xem
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SHIPPER')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByOrderId(@PathVariable Long orderId) {
        ShipmentResponse shipment = shipmentService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Lấy thông tin giao hàng thành công"));
    }
    
    /**
     * Lấy danh sách giao hàng của shipper
     * GET /api/shipments/shipper/{shipperId}
     * Admin/Staff xem tất cả, Shipper xem của mình
     */
    @GetMapping("/shipper/{shipperId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or #shipperId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getShipmentsByShipperId(@PathVariable Long shipperId) {
        List<ShipmentResponse> shipments = shipmentService.getShipmentsByShipperId(shipperId);
        return ResponseEntity.ok(ApiResponse.success(shipments, "Lấy danh sách giao hàng thành công"));
    }
    
    /**
     * Lấy danh sách giao hàng theo trạng thái
     * GET /api/shipments/status/{status}
     * Chỉ Admin và Staff
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getShipmentsByStatus(@PathVariable String status) {
        List<ShipmentResponse> shipments = shipmentService.getShipmentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(shipments, "Lấy danh sách giao hàng thành công"));
    }
    
    /**
     * Cập nhật trạng thái giao hàng
     * PATCH /api/shipments/{id}/status
     * Shipper cập nhật đơn của mình, Staff/Admin cập nhật tất cả
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SHIPPER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ShipmentResponse shipment = shipmentService.updateShipmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Cập nhật trạng thái giao hàng thành công"));
    }
    
    /**
     * Gán shipper cho đơn giao hàng
     * PATCH /api/shipments/{id}/assign-shipper
     * Chỉ Staff và Admin
     */
    @PatchMapping("/{id}/assign-shipper")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> assignShipper(
            @PathVariable Long id,
            @RequestParam Long shipperId) {
        ShipmentResponse shipment = shipmentService.assignShipper(id, shipperId);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Gán shipper thành công"));
    }
    
    // ========================================
    // ENHANCEMENTS - Phase 2: Attachment Management
    // ========================================
    // NOTE: Simplified implementation for attachment metadata management
    // Production implementation should include:
    // - Actual file upload handling with MultipartFile
    // - File validation (size, format, virus scan)
    // - Storage service integration (AWS S3, Azure Blob Storage, etc.)
    // - Image compression and thumbnail generation
    // - CDN integration for delivery
    
    /**
     * Thêm attachment cho shipment (Proof of Delivery)
     * POST /api/shipments/{id}/attachments
     * Shipper upload ảnh giao hàng, Staff/Admin có thể upload
     * 
     * TODO: Implement actual file upload with MultipartFile
     * @RequestParam("file") MultipartFile file
     */
    @PostMapping("/{id}/attachments")
    @PreAuthorize("hasAnyRole('SHIPPER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> addAttachment(
            @PathVariable Long id,
            @RequestParam String fileUrl,
            @RequestParam(required = false) String fileType) {
        
        // TODO: Implement file upload logic
        // 1. Validate file (size, format)
        // 2. Upload to storage service
        // 3. Save attachment metadata to database
        
        String message = String.format(
            "Attachment management endpoint. Shipment ID: %d, FileURL: %s, FileType: %s. " +
            "TODO: Implement actual file upload with storage service.",
            id, fileUrl, fileType
        );
        
        return ResponseEntity.ok(ApiResponse.success(message, "Endpoint sẵn sàng cho implementation"));
    }
    
    /**
     * Lấy danh sách attachments của shipment
     * GET /api/shipments/{id}/attachments
     * Staff/Admin/Shipper/Customer có thể xem
     * 
     * TODO: Query attachments from database
     */
    @GetMapping("/{id}/attachments")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SHIPPER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> getAttachments(@PathVariable Long id) {
        
        // TODO: Implement query logic
        // AttachmentRepository.findByShipmentId(id)
        
        String message = String.format(
            "Get attachments endpoint. Shipment ID: %d. " +
            "TODO: Query attachments from AttachmentRepository.",
            id
        );
        
        return ResponseEntity.ok(ApiResponse.success(message, "Endpoint sẵn sàng cho implementation"));
    }
    
    /**
     * Xóa attachment
     * DELETE /api/shipments/{id}/attachments/{attachmentId}
     * Chỉ Staff và Admin, hoặc Shipper xóa của mình
     * 
     * TODO: Delete attachment file and metadata
     */
    @DeleteMapping("/{id}/attachments/{attachmentId}")
    @PreAuthorize("hasAnyRole('SHIPPER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteAttachment(
            @PathVariable Long id,
            @PathVariable Long attachmentId) {
        
        // TODO: Implement delete logic
        // 1. Delete file from storage service
        // 2. Delete metadata from database
        
        String message = String.format(
            "Delete attachment endpoint. Shipment ID: %d, Attachment ID: %d. " +
            "TODO: Delete file from storage and database.",
            id, attachmentId
        );
        
        return ResponseEntity.ok(ApiResponse.success(message, "Endpoint sẵn sàng cho implementation"));
    }
    
    // ========================================
    // PHASE 3: STATISTICS & ANALYTICS
    // ========================================
    
    /**
     * Lấy overall shipment statistics
     * GET /api/shipments/statistics
     * Chỉ Admin và Staff
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Overall shipment statistics",
        description = "Tổng quan thống kê shipments: counts by status, success rate, average delivery time. Chỉ ADMIN và STAFF."
    )
    public ResponseEntity<ApiResponse<ShipmentService.ShipmentStatistics>> getShipmentStatistics() {
        ShipmentService.ShipmentStatistics stats = shipmentService.getShipmentStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê shipments thành công"));
    }
}
