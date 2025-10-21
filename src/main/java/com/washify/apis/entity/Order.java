package com.washify.apis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho đơn hàng của khách hàng
 * Hỗ trợ Soft Delete - không xóa vật lý khỏi database
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE orders SET deleted_at = NOW() WHERE id = ?") // Soft delete
@Where(clause = "deleted_at IS NULL") // Chỉ query các record chưa bị xóa
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_code", unique = true, nullable = false, length = 50)
    private String orderCode; // Mã đơn hàng (VD: WF202510210001)
    
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
    
    @Column(name = "deleted_at") // Timestamp khi bị xóa (soft delete)
    private LocalDateTime deletedAt;
    
    // One-to-Many: Một order có nhiều order items
    @JsonIgnore // Tránh circular reference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();
    
    // One-to-One: Một order có một payment
    @JsonIgnore // Tránh circular reference Order ↔ Payment
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
    
    // One-to-One: Một order có tối đa một shipment
    @JsonIgnore // Tránh circular reference Order ↔ Shipment
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipment shipment;
    
    // One-to-Many: Một order có nhiều reviews
    @JsonIgnore // Tránh circular reference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
    
    // Many-to-Many: Order có nhiều promotions, Promotion áp dụng cho nhiều orders
    @JsonIgnore // Tránh circular reference
    @ManyToMany
    @JoinTable(
        name = "order_promotions",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private Set<Promotion> promotions = new HashSet<>();
    
    // One-to-Many: Một order có nhiều attachments
    @JsonIgnore // Tránh circular reference
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
