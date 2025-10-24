package com.washify.apis.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity đại diện cho chi tiết từng dịch vụ trong đơn hàng
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Only use specific fields for equals/hashCode
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Include ID in equals/hashCode
    private Long id;
    
    // Many-to-One: Nhiều order items thuộc một order
    @JsonIgnoreProperties({"orderItems", "payment", "shipment", "reviews", "promotions", "attachments"}) // Tránh circular reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Đơn hàng chứa item này
    
    // Many-to-One: Nhiều order items sử dụng một service
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service; // Dịch vụ được chọn
    
    @Column(nullable = false)
    private Integer quantity = 1; // Số lượng (VD: 5kg, 3 áo, etc.)
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Giá tại thời điểm đặt hàng
}
