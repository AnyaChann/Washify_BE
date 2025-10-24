package com.washify.apis.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Only use specific fields for equals/hashCode
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Include ID in equals/hashCode
    private Long id;
    
    // One-to-One: Mỗi order chỉ có một payment
    @JsonIgnoreProperties({"payment", "shipment", "orderItems", "reviews", "promotions", "attachments"}) // Chỉ serialize thông tin cơ bản của Order
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
    
    @Column(name = "transaction_id")
    private String transactionId; // Mã giao dịch từ payment gateway
    
    @Column(name = "payment_url", length = 500)
    private String paymentUrl; // URL thanh toán (cho MOMO, VNPAY, etc.)
    
    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode; // QR code cho thanh toán (base64 hoặc URL)
    
    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse; // Response từ payment gateway (JSON)
    
    /**
     * Enum định nghĩa các phương thức thanh toán
     */
    public enum PaymentMethod {
        CASH,           // Tiền mặt (Tại quầy / COD)
        MOMO            // MoMo Wallet
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
