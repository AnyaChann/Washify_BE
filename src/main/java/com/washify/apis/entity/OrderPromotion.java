package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity join table cho quan hệ Many-to-Many giữa Order và Promotion
 * Lưu thông tin chi tiết về việc áp dụng mã khuyến mãi cho đơn hàng
 * 
 * Note: Hiện tại đang dùng @JoinTable trong Order.java
 * File này để sẵn cho trường hợp cần lưu thêm thông tin về discount applied
 */
@Entity
@Table(name = "order_promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class OrderPromotion implements Serializable {
    
    /**
     * Composite Primary Key cho bảng order_promotions
     */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class OrderPromotionId implements Serializable {
        
        @Column(name = "order_id")
        private Long orderId;
        
        @Column(name = "promotion_id")
        private Long promotionId;
    }
    
    @EmbeddedId
    @EqualsAndHashCode.Include
    private OrderPromotionId id;
    
    // Many-to-One: Nhiều order_promotions thuộc một order
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId") // Map với orderId trong composite key
    @JoinColumn(name = "order_id")
    private Order order;
    
    // Many-to-One: Nhiều order_promotions thuộc một promotion
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("promotionId") // Map với promotionId trong composite key
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
    
    // TODO: Có thể thêm các thuộc tính mở rộng ở đây
    // @Column(name = "discount_amount", precision = 10, scale = 2)
    // private BigDecimal discountAmount; // Số tiền giảm thực tế (sau khi tính toán)
    
    // @CreationTimestamp
    // @Column(name = "applied_at")
    // private LocalDateTime appliedAt; // Thời gian áp dụng mã
    
    // @Column(name = "is_valid")
    // private Boolean isValid = true; // Mã có hợp lệ khi áp dụng không (để audit)
    
    // @Column(name = "reason")
    // private String reason; // Lý do nếu mã không hợp lệ
    
    /**
     * Constructor tiện lợi để tạo OrderPromotion từ Order và Promotion
     */
    public OrderPromotion(Order order, Promotion promotion) {
        this.order = order;
        this.promotion = promotion;
        this.id = new OrderPromotionId(order.getId(), promotion.getId());
    }
}
