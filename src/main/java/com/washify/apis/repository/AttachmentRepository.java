package com.washify.apis.repository;

import com.washify.apis.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface cho Attachment entity
 * Cung cấp các phương thức truy vấn database cho bảng attachments
 */
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    
    /**
     * Tìm tất cả attachments của một order
     * @param orderId ID của order
     * @return Danh sách attachments
     */
    List<Attachment> findByOrderId(Long orderId);
    
    /**
     * Tìm tất cả attachments của một shipment
     * @param shipmentId ID của shipment
     * @return Danh sách attachments
     */
    List<Attachment> findByShipmentId(Long shipmentId);
    
    /**
     * Tìm attachments theo loại file
     * @param fileType Loại file (VD: image/jpeg, application/pdf)
     * @return Danh sách attachments
     */
    List<Attachment> findByFileType(String fileType);
    
    /**
     * Xóa tất cả attachments của một order
     * @param orderId ID của order
     */
    void deleteByOrderId(Long orderId);
    
    /**
     * Xóa tất cả attachments của một shipment
     * @param shipmentId ID của shipment
     */
    void deleteByShipmentId(Long shipmentId);
}
