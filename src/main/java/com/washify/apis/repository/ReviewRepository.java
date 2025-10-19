package com.washify.apis.repository;

import com.washify.apis.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Review entity
 * Cung cấp các phương thức truy vấn database cho bảng reviews
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Tìm review theo order ID
     * @param orderId ID của order
     * @return Optional chứa Review nếu tìm thấy
     */
    Optional<Review> findByOrderId(Long orderId);
    
    /**
     * Tìm tất cả reviews của một user
     * @param userId ID của user
     * @return Danh sách reviews
     */
    List<Review> findByUserId(Long userId);
    
    /**
     * Tìm reviews theo rating
     * @param rating Điểm đánh giá (1-5)
     * @return Danh sách reviews
     */
    List<Review> findByRating(Integer rating);
    
    /**
     * Tìm reviews có rating >= giá trị cho trước
     * @param rating Điểm đánh giá tối thiểu
     * @return Danh sách reviews
     */
    List<Review> findByRatingGreaterThanEqual(Integer rating);
    
    /**
     * Tính rating trung bình
     * @return Rating trung bình
     */
    @Query("SELECT AVG(r.rating) FROM Review r")
    Double findAverageRating();
    
    /**
     * Đếm số reviews theo rating
     * @param rating Điểm đánh giá
     * @return Số lượng reviews
     */
    long countByRating(Integer rating);
}
