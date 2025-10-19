package com.washify.apis.controller;

import com.washify.apis.dto.request.ReviewRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.ReviewResponse;
import com.washify.apis.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến Review
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    /**
     * Tạo đánh giá mới
     * POST /api/reviews
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestParam Long userId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.createReview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(review, "Tạo đánh giá thành công"));
    }
    
    /**
     * Lấy thông tin đánh giá theo ID
     * GET /api/reviews/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable Long id) {
        ReviewResponse review = reviewService.getReviewById(id);
        return ResponseEntity.ok(ApiResponse.success(review, "Lấy thông tin đánh giá thành công"));
    }
    
    /**
     * Lấy đánh giá theo order ID
     * GET /api/reviews/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewByOrderId(@PathVariable Long orderId) {
        ReviewResponse review = reviewService.getReviewByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(review, "Lấy đánh giá thành công"));
    }
    
    /**
     * Lấy danh sách đánh giá của user
     * GET /api/reviews/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Lấy danh sách đánh giá thành công"));
    }
    
    /**
     * Lấy tất cả đánh giá
     * GET /api/reviews
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews() {
        List<ReviewResponse> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(ApiResponse.success(reviews, "Lấy danh sách đánh giá thành công"));
    }
    
    /**
     * Lấy đánh giá theo rating
     * GET /api/reviews/rating/{rating}
     */
    @GetMapping("/rating/{rating}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByRating(@PathVariable Integer rating) {
        List<ReviewResponse> reviews = reviewService.getReviewsByRating(rating);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Lấy danh sách đánh giá thành công"));
    }
    
    /**
     * Tính rating trung bình
     * GET /api/reviews/average-rating
     */
    @GetMapping("/average-rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRating() {
        Double averageRating = reviewService.getAverageRating();
        return ResponseEntity.ok(ApiResponse.success(averageRating, "Lấy rating trung bình thành công"));
    }
    
    /**
     * Xóa đánh giá
     * DELETE /api/reviews/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa đánh giá thành công"));
    }
}
