package com.washify.apis.entity;

// Import các annotation JPA để map class với database table
import jakarta.persistence.*;
// Import Lombok để tự động generate getters, setters, constructors
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Import annotation Hibernate để tự động set timestamp
import org.hibernate.annotations.CreationTimestamp;

// Import class để xử lý ngày giờ
import java.time.LocalDateTime;

/**
 * Entity class đại diện cho bảng attachments trong database
 * Dùng để lưu trữ các file đính kèm (hình ảnh, tài liệu) cho đơn hàng hoặc shipment
 */
@Entity // Đánh dấu đây là một JPA entity, sẽ được map với bảng trong database
@Table(name = "attachments") // Chỉ định tên bảng trong database là "attachments"
@Data // Lombok: Tự động generate getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Tự động tạo constructor không tham số (cần cho JPA)
@AllArgsConstructor // Lombok: Tự động tạo constructor với tất cả tham số
public class Attachment {
    
    @Id // Đánh dấu đây là khóa chính (Primary Key) của bảng
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng giá trị ID (AUTO_INCREMENT)
    private Long id; // ID duy nhất của attachment
    
    /**
     * Quan hệ Many-to-One với Order
     * Nhiều attachments có thể thuộc về một order
     */
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading: chỉ load Order khi thực sự cần thiết (tối ưu performance)
    @JoinColumn(name = "order_id") // Tên cột foreign key trong bảng attachments
    private Order order; // Đơn hàng mà file này được đính kèm (nullable - có thể null)
    
    /**
     * Quan hệ Many-to-One với Shipment
     * Nhiều attachments có thể thuộc về một shipment
     */
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading: chỉ load Shipment khi cần
    @JoinColumn(name = "shipment_id") // Tên cột foreign key trong bảng attachments
    private Shipment shipment; // Đơn giao hàng mà file này được đính kèm (nullable - có thể null)
    
    /**
     * URL hoặc đường dẫn đến file đã upload
     * VD: https://storage.washify.com/files/invoice_123.pdf
     * Hoặc: /uploads/2025/10/receipt_456.jpg
     */
    @Column(name = "file_url", nullable = false, length = 255) // Map với cột file_url, bắt buộc nhập, max 255 ký tự
    private String fileUrl; // Đường dẫn URL của file
    
    /**
     * Loại file được upload
     * VD: "image/jpeg", "image/png", "application/pdf"
     */
    @Column(name = "file_type", length = 50) // Map với cột file_type, max 50 ký tự, có thể null
    private String fileType; // MIME type của file (tuỳ chọn)
    
    /**
     * Thời gian file được upload lên hệ thống
     * Tự động set khi record được tạo
     */
    @CreationTimestamp // Hibernate tự động set giá trị = thời gian hiện tại khi tạo mới record
    @Column(name = "uploaded_at", updatable = false) // Map với cột uploaded_at, không được update sau khi tạo
    private LocalDateTime uploadedAt; // Thời điểm upload file
}
