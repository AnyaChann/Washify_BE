package com.washify.apis.controller;

import com.washify.apis.dto.request.AttachmentRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.AttachmentResponse;
import com.washify.apis.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến Attachment
 * Phase 3: Attachment Management
 */
@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachments", description = "Quản lý file đính kèm - 🔒 Authenticated users")
public class AttachmentController {
    
    private final AttachmentService attachmentService;
    
    // ========================================
    // PHASE 3: ATTACHMENT MANAGEMENT
    // ========================================
    
    /**
     * Upload file đính kèm (MultipartFile)
     * POST /api/attachments/upload
     * Staff/Customer có thể upload cho order/shipment của mình
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    @Operation(summary = "Upload file", description = "Upload file đính kèm cho order hoặc shipment")
    public ResponseEntity<ApiResponse<AttachmentResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long shipmentId) {
        
        AttachmentResponse attachment = attachmentService.uploadFile(file, orderId, shipmentId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(attachment, "Upload file thành công"));
    }
    
    /**
     * Tạo attachment từ URL có sẵn
     * POST /api/attachments
     * Dùng khi file đã được upload lên storage khác (S3, CDN, etc.)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    @Operation(summary = "Tạo attachment từ URL", description = "Tạo attachment record từ URL file đã upload")
    public ResponseEntity<ApiResponse<AttachmentResponse>> createAttachment(
            @Valid @RequestBody AttachmentRequest request) {
        
        AttachmentResponse attachment = attachmentService.createAttachment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(attachment, "Tạo attachment thành công"));
    }
    
    /**
     * Lấy thông tin attachment theo ID
     * GET /api/attachments/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    @Operation(summary = "Lấy thông tin attachment", description = "Lấy thông tin chi tiết của một attachment")
    public ResponseEntity<ApiResponse<AttachmentResponse>> getAttachmentById(@PathVariable Long id) {
        AttachmentResponse attachment = attachmentService.getAttachmentById(id);
        return ResponseEntity.ok(ApiResponse.success(attachment, "Lấy thông tin attachment thành công"));
    }
    
    /**
     * Lấy danh sách attachments của order
     * GET /api/attachments/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    @Operation(summary = "Lấy attachments của order", description = "Lấy tất cả file đính kèm của một order")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> getAttachmentsByOrderId(
            @PathVariable Long orderId) {
        
        List<AttachmentResponse> attachments = attachmentService.getAttachmentsByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(attachments, "Lấy danh sách attachments thành công"));
    }
    
    /**
     * Lấy danh sách attachments của shipment
     * GET /api/attachments/shipment/{shipmentId}
     */
    @GetMapping("/shipment/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    @Operation(summary = "Lấy attachments của shipment", description = "Lấy tất cả file đính kèm của một shipment")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> getAttachmentsByShipmentId(
            @PathVariable Long shipmentId) {
        
        List<AttachmentResponse> attachments = attachmentService.getAttachmentsByShipmentId(shipmentId);
        return ResponseEntity.ok(ApiResponse.success(attachments, "Lấy danh sách attachments thành công"));
    }
    
    /**
     * Download file đính kèm
     * GET /api/attachments/{id}/download
     * Trả về file để download
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    @Operation(summary = "Download file", description = "Download file đính kèm")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            Path filePath = attachmentService.getFilePath(id);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                // Lấy tên file từ path
                String filename = filePath.getFileName().toString();
                
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("File không thể đọc được");
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi download file: " + e.getMessage());
        }
    }
    
    /**
     * Xóa attachment
     * DELETE /api/attachments/{id}
     * Admin/Staff hoặc owner có thể xóa
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Xóa attachment", description = "Xóa file đính kèm và record trong database")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa attachment thành công"));
    }
}
