package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho thông báo gửi đến người dùng
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many-to-One: Nhiều notifications thuộc một user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người nhận thông báo
    
    @Column(length = 100)
    private String title; // Tiêu đề thông báo
    
    @Column(columnDefinition = "TEXT")
    private String message; // Nội dung thông báo
    
    @Column(length = 50)
    private String type; // Loại thông báo (có thể dùng String thay vì enum để đơn giản)
    
    @Column(name = "related_id")
    private Long relatedId; // ID liên quan (order, payment, etc.)
    
    @Column(name = "is_read")
    private Boolean isRead = false; // Trạng thái đã đọc/chưa đọc
    
    @Column(name = "read_at")
    private LocalDateTime readAt; // Thời gian đọc thông báo
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Thời gian tạo thông báo
}
