package com.washify.apis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho thông tin giao hàng tận nơi
 */
@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Only use specific fields for equals/hashCode
public class Shipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Include ID in equals/hashCode
    private Long id;
    
    // One-to-One: Mỗi order có tối đa một shipment
    @JsonIgnoreProperties({"payment", "shipment", "orderItems", "reviews", "promotions", "attachments"}) // Chỉ serialize thông tin cơ bản của Order
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", unique = true)
    private Order order; // Đơn hàng được giao
    
    // Many-to-One: Nhiều shipments thuộc một user (người nhận)
    @JsonIgnoreProperties({"orders", "reviews", "notifications", "roles"}) // Tránh circular reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người nhận hàng
    
    // Many-to-One: Nhiều shipments được giao bởi một shipper
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id", nullable = true)
    private Shipper shipper; // Người giao hàng (nullable)
    
    @Column(nullable = false, length = 255)
    private String address; // Địa chỉ giao hàng
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING; // Trạng thái giao hàng
    
    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate; // Thời gian giao hàng (dự kiến hoặc thực tế)
    
    @Column(name = "shipper_name", length = 100)
    private String shipperName; // Tên shipper (backup nếu không có trong bảng shippers)
    
    @Column(name = "shipper_phone", length = 20)
    private String shipperPhone; // SĐT shipper (backup)
    
    // One-to-Many: Một shipment có nhiều attachments
    @JsonIgnore // Tránh circular reference
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL)
    private Set<Attachment> attachments = new HashSet<>();
    
    /**
     * Enum định nghĩa trạng thái giao hàng
     */
    public enum DeliveryStatus {
        PENDING,   // Chờ giao
        SHIPPING,  // Đang giao
        DELIVERED, // Đã giao
        CANCELLED  // Đã hủy
    }
}
