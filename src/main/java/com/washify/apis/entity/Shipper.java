package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho người giao hàng (shipper nội bộ hoặc đối tác)
 */
@Entity
@Table(name = "shippers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipper {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name; // Tên shipper
    
    @Column(length = 20)
    private String phone; // Số điện thoại liên hệ
    
    @Column(name = "vehicle_number", length = 50)
    private String vehicleNumber; // Biển số xe
    
    @Column(name = "is_active")
    private Boolean isActive = true; // Trạng thái hoạt động
    
    // One-to-Many: Một shipper có nhiều shipments
    @OneToMany(mappedBy = "shipper")
    private Set<Shipment> shipments = new HashSet<>();
}
