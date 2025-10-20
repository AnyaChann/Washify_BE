package com.washify.apis.repository;

import com.washify.apis.entity.OrderPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho entity OrderPromotion (join table giữa Order và Promotion)
 * 
 * Note: Hiện tại dự án đang dùng @JoinTable trong Order.java
 * Repository này dành cho trường hợp cần query trực tiếp bảng order_promotions
 * hoặc khi mở rộng thêm thuộc tính vào join table (ví dụ: discount_amount, applied_at)
 */
@Repository
public interface OrderPromotionRepository extends JpaRepository<OrderPromotion, OrderPromotion.OrderPromotionId> {
    
    /**
     * Tìm tất cả order promotions theo order ID
     * @param orderId ID của order
     * @return Danh sách OrderPromotion của order đó
     */
    List<OrderPromotion> findByOrderId(Long orderId);
    
    /**
     * Tìm tất cả order promotions theo promotion ID
     * @param promotionId ID của promotion
     * @return Danh sách OrderPromotion sử dụng promotion đó
     */
    List<OrderPromotion> findByPromotionId(Long promotionId);
    
    /**
     * Kiểm tra xem order có sử dụng promotion cụ thể không
     * @param orderId ID của order
     * @param promotionId ID của promotion
     * @return true nếu order có sử dụng promotion này
     */
    boolean existsByOrderIdAndPromotionId(Long orderId, Long promotionId);
    
    /**
     * Đếm số lần một promotion được sử dụng
     * @param promotionId ID của promotion
     * @return Số lần promotion được sử dụng
     */
    long countByPromotionId(Long promotionId);
    
    /**
     * Xóa order promotion theo order ID và promotion ID
     * @param orderId ID của order
     * @param promotionId ID của promotion
     */
    void deleteByOrderIdAndPromotionId(Long orderId, Long promotionId);
    
    /**
     * Xóa tất cả order promotions của một order
     * @param orderId ID của order
     */
    void deleteByOrderId(Long orderId);
    
    /**
     * Tìm tất cả orders đã sử dụng một promotion code cụ thể
     * @param promotionCode Mã promotion
     * @return Danh sách OrderPromotion sử dụng mã đó
     */
    @Query("SELECT op FROM OrderPromotion op JOIN op.promotion p WHERE p.code = :promotionCode")
    List<OrderPromotion> findByPromotionCode(String promotionCode);
}
