package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho các dịch vụ giặt là
 */
@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name; // Tên dịch vụ (VD: Giặt khô, Giặt ướt, Là hơi)
    
    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả chi tiết dịch vụ
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Giá dịch vụ (có thể theo kg hoặc item)
    
    @Column(name = "estimated_time")
    private Integer estimatedTime; // Thời gian ước tính (đơn vị: giờ)
    
    @Column(name = "is_active")
    private Boolean isActive = true; // Trạng thái hoạt động của dịch vụ
    
    // One-to-Many: Một service có nhiều order items
    @OneToMany(mappedBy = "service")
    private Set<OrderItem> orderItems = new HashSet<>();
}
