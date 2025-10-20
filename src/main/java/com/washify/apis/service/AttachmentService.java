package com.washify.apis.service;

import com.washify.apis.dto.request.AttachmentRequest;
import com.washify.apis.dto.response.AttachmentResponse;
import com.washify.apis.entity.Attachment;
import com.washify.apis.entity.Order;
import com.washify.apis.entity.Shipment;
import com.washify.apis.repository.AttachmentRepository;
import com.washify.apis.repository.OrderRepository;
import com.washify.apis.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Attachment
 * Phase 3: Attachment Management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentService {
    
    private final AttachmentRepository attachmentRepository;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    
    // Đường dẫn lưu file (trong thực tế nên dùng AWS S3, Azure Blob, etc.)
    private static final String UPLOAD_DIR = "uploads/";
    
    /**
     * Upload file và tạo attachment mới
     * Note: Đây là implementation đơn giản với local storage
     * Production nên dùng cloud storage (S3, Azure Blob)
     */
    public AttachmentResponse uploadFile(MultipartFile file, Long orderId, Long shipmentId) {
        if (file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }
        
        // Validate: phải có ít nhất orderId hoặc shipmentId
        if (orderId == null && shipmentId == null) {
            throw new RuntimeException("Phải cung cấp orderId hoặc shipmentId");
        }
        
        try {
            // Tạo tên file unique
            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID().toString() + "_" + originalFilename;
            
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Lưu file vào disk
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Tạo attachment entity
            Attachment attachment = new Attachment();
            attachment.setFileUrl(UPLOAD_DIR + filename);
            attachment.setFileType(file.getContentType());
            
            // Set order nếu có
            if (orderId != null) {
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy order với ID: " + orderId));
                attachment.setOrder(order);
            }
            
            // Set shipment nếu có
            if (shipmentId != null) {
                Shipment shipment = shipmentRepository.findById(shipmentId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy shipment với ID: " + shipmentId));
                attachment.setShipment(shipment);
            }
            
            Attachment savedAttachment = attachmentRepository.save(attachment);
            return mapToAttachmentResponse(savedAttachment);
            
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload file: " + e.getMessage());
        }
    }
    
    /**
     * Tạo attachment từ URL (file đã được upload sẵn)
     */
    public AttachmentResponse createAttachment(AttachmentRequest request) {
        // Validate
        if (request.getOrderId() == null && request.getShipmentId() == null) {
            throw new RuntimeException("Phải cung cấp orderId hoặc shipmentId");
        }
        
        Attachment attachment = new Attachment();
        attachment.setFileUrl(request.getFileUrl());
        attachment.setFileType(request.getFileType());
        
        // Set order nếu có
        if (request.getOrderId() != null) {
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy order với ID: " + request.getOrderId()));
            attachment.setOrder(order);
        }
        
        // Set shipment nếu có
        if (request.getShipmentId() != null) {
            Shipment shipment = shipmentRepository.findById(request.getShipmentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy shipment với ID: " + request.getShipmentId()));
            attachment.setShipment(shipment);
        }
        
        Attachment savedAttachment = attachmentRepository.save(attachment);
        return mapToAttachmentResponse(savedAttachment);
    }
    
    /**
     * Lấy thông tin attachment theo ID
     */
    @Transactional(readOnly = true)
    public AttachmentResponse getAttachmentById(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy attachment với ID: " + attachmentId));
        return mapToAttachmentResponse(attachment);
    }
    
    /**
     * Lấy danh sách attachments của order
     */
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachmentsByOrderId(Long orderId) {
        // Kiểm tra order có tồn tại không
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Không tìm thấy order với ID: " + orderId);
        }
        
        return attachmentRepository.findByOrderId(orderId).stream()
                .map(this::mapToAttachmentResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách attachments của shipment
     */
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachmentsByShipmentId(Long shipmentId) {
        // Kiểm tra shipment có tồn tại không
        if (!shipmentRepository.existsById(shipmentId)) {
            throw new RuntimeException("Không tìm thấy shipment với ID: " + shipmentId);
        }
        
        return attachmentRepository.findByShipmentId(shipmentId).stream()
                .map(this::mapToAttachmentResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Xóa attachment
     */
    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy attachment với ID: " + attachmentId));
        
        // Xóa file từ disk (nếu là local storage)
        try {
            Path filePath = Paths.get(attachment.getFileUrl());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Log error nhưng vẫn xóa record trong DB
            System.err.println("Không thể xóa file: " + e.getMessage());
        }
        
        attachmentRepository.delete(attachment);
    }
    
    /**
     * Download/Get file path
     */
    @Transactional(readOnly = true)
    public Path getFilePath(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy attachment với ID: " + attachmentId));
        
        Path filePath = Paths.get(attachment.getFileUrl());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File không tồn tại");
        }
        
        return filePath;
    }
    
    /**
     * Map Entity sang DTO Response
     */
    private AttachmentResponse mapToAttachmentResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .orderId(attachment.getOrder() != null ? attachment.getOrder().getId() : null)
                .shipmentId(attachment.getShipment() != null ? attachment.getShipment().getId() : null)
                .fileUrl(attachment.getFileUrl())
                .fileType(attachment.getFileType())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }
}
