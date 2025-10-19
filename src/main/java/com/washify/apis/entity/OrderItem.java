package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
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
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many-to-One: Nhiều order items thuộc một order
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
