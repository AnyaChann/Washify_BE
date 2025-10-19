package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho thông tin thanh toán của đơn hàng
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // One-to-One: Mỗi order chỉ có một payment
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private Order order; // Đơn hàng được thanh toán
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod; // Phương thức thanh toán
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING; // Trạng thái thanh toán
    
    @CreationTimestamp
    @Column(name = "payment_date")
    private LocalDateTime paymentDate; // Thời gian thanh toán
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // Số tiền thanh toán
    
    /**
     * Enum định nghĩa các phương thức thanh toán
     */
    public enum PaymentMethod {
        CASH,   // Tiền mặt
        CARD,   // Thẻ ngân hàng
        ONLINE  // Thanh toán online (Momo, ZaloPay, etc.)
    }
    
    /**
     * Enum định nghĩa trạng thái thanh toán
     */
    public enum PaymentStatus {
        PENDING, // Chờ thanh toán
        PAID,    // Đã thanh toán
        FAILED   // Thanh toán thất bại
    }
}
