package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho đánh giá của khách hàng về đơn hàng
 */
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many-to-One: Nhiều reviews thuộc một order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Đơn hàng được đánh giá
    
    // Many-to-One: Nhiều reviews của một user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người đánh giá
    
    @Column(nullable = false)
    private Integer rating; // Điểm đánh giá từ 1-5 sao
    
    @Column(columnDefinition = "TEXT")
    private String comment; // Nhận xét chi tiết
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Thời gian đánh giá
}
