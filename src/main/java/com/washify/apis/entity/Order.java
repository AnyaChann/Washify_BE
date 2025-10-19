package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho đơn hàng của khách hàng
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many-to-One: Nhiều orders thuộc một user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Khách hàng đặt đơn
    
    // Many-to-One: Nhiều orders thuộc một branch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch; // Chi nhánh xử lý đơn hàng
    
    @CreationTimestamp
    @Column(name = "order_date")
    private LocalDateTime orderDate; // Thời gian đặt đơn
    
    @Enumerated(EnumType.STRING) // Lưu dạng text trong DB
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING; // Trạng thái đơn hàng
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO; // Tổng tiền đơn hàng
    
    @Column(columnDefinition = "TEXT")
    private String notes; // Ghi chú thêm
    
    // One-to-Many: Một order có nhiều order items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();
    
    // One-to-One: Một order có một payment
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
    
    // One-to-One: Một order có tối đa một shipment
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipment shipment;
    
    // One-to-Many: Một order có nhiều reviews
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
    
    // Many-to-Many: Order có nhiều promotions, Promotion áp dụng cho nhiều orders
    @ManyToMany
    @JoinTable(
        name = "order_promotions",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private Set<Promotion> promotions = new HashSet<>();
    
    // One-to-Many: Một order có nhiều attachments
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<Attachment> attachments = new HashSet<>();
    
    /**
     * Enum định nghĩa các trạng thái của đơn hàng
     */
    public enum OrderStatus {
        PENDING,      // Chờ xử lý
        IN_PROGRESS,  // Đang xử lý
        COMPLETED,    // Hoàn thành
        CANCELLED     // Đã hủy
    }
}
