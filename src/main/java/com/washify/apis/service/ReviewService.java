package com.washify.apis.service;

import com.washify.apis.dto.request.ReviewRequest;
import com.washify.apis.dto.response.ReviewResponse;
import com.washify.apis.entity.Order;
import com.washify.apis.entity.Review;
import com.washify.apis.entity.User;
import com.washify.apis.repository.OrderRepository;
import com.washify.apis.repository.ReviewRepository;
import com.washify.apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Review
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    
    /**
     * Tạo đánh giá mới
     */
    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        // Tìm order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + request.getOrderId()));
        
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));
        
        // Kiểm tra user có phải là người đặt đơn không
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền đánh giá đơn hàng này");
        }
        
        // Kiểm tra đơn hàng đã hoàn thành chưa
        if (order.getStatus() != Order.OrderStatus.COMPLETED) {
            throw new RuntimeException("Chỉ có thể đánh giá đơn hàng đã hoàn thành");
        }
        
        // Kiểm tra đã đánh giá chưa
        if (reviewRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new RuntimeException("Bạn đã đánh giá đơn hàng này rồi");
        }
        
        // Tạo review
        Review review = new Review();
        review.setOrder(order);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        Review savedReview = reviewRepository.save(review);
        return mapToReviewResponse(savedReview);
    }
    
    /**
     * Lấy thông tin đánh giá theo ID
     */
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + reviewId));
        return mapToReviewResponse(review);
    }
    
    /**
     * Lấy đánh giá theo order ID
     */
    @Transactional(readOnly = true)
    public ReviewResponse getReviewByOrderId(Long orderId) {
        Review review = reviewRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá cho đơn hàng ID: " + orderId));
        return mapToReviewResponse(review);
    }
    
    /**
     * Lấy danh sách đánh giá của user
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách đánh giá theo service
     * PHASE 3: Operational Enhancements
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByServiceId(Long serviceId) {
        return reviewRepository.findByServiceId(serviceId).stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy tất cả đánh giá
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy đánh giá theo rating
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating).stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Tính rating trung bình
     */
    @Transactional(readOnly = true)
    public Double getAverageRating() {
        return reviewRepository.findAverageRating();
    }
    
    /**
     * Xóa đánh giá
     */
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Không tìm thấy đánh giá với ID: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }
    
    /**
     * Map Entity sang DTO Response
     */
    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .orderId(review.getOrder().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
