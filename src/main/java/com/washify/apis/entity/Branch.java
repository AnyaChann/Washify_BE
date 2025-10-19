package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho chi nhánh cửa tiệm giặt là
 */
@Entity
@Table(name = "branches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name; // Tên chi nhánh
    
    @Column(nullable = false, length = 255)
    private String address; // Địa chỉ chi nhánh
    
    @Column(length = 20)
    private String phone; // Số điện thoại chi nhánh
    
    @Column(name = "manager_name", length = 100)
    private String managerName; // Tên quản lý chi nhánh
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Thời gian tạo chi nhánh
    
    // One-to-Many: Một branch có nhiều users
    @OneToMany(mappedBy = "branch")
    private Set<User> users = new HashSet<>();
    
    // One-to-Many: Một branch có nhiều orders
    @OneToMany(mappedBy = "branch")
    private Set<Order> orders = new HashSet<>();
}
