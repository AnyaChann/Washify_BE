package com.washify.apis.repository;

import com.washify.apis.entity.Order;
import com.washify.apis.entity.OrderItem;
import com.washify.apis.entity.Promotion;
import com.washify.apis.entity.User;
import com.washify.apis.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface cho Order entity
 * Cung cấp các phương thức truy vấn database cho bảng orders
 * Hỗ trợ Soft Delete
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Tìm tất cả orders của một user
     * @param user User đặt đơn
     * @return Danh sách orders
     */
    List<Order> findByUser(User user);
    
    /**
     * Tìm orders theo user ID
     * @param userId ID của user
     * @return Danh sách orders
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * Tìm orders theo trạng thái
     * @param status Trạng thái đơn hàng
     * @return Danh sách orders
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Tìm orders của user theo trạng thái
     * @param userId ID của user
     * @param status Trạng thái đơn hàng
     * @return Danh sách orders
     */
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    /**
     * Tìm orders trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách orders
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Tìm orders theo branch ID
     * @param branchId ID của chi nhánh
     * @return Danh sách orders
     */
    List<Order> findByBranchId(Long branchId);
    
    /**
     * Tìm order theo order code
     * @param orderCode Mã đơn hàng (unique)
     * @return Optional<Order>
     */
    Optional<Order> findByOrderCode(String orderCode);
    
    /**
     * Tìm order theo ID với basic eager loading (chỉ user và branch)
     * @param id ID của order
     * @return Optional<Order> với basic info đã load
     */
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.user " +
           "LEFT JOIN FETCH o.branch " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Lấy order items của một order
     * @param orderId ID của order
     * @return List<OrderItem>
     */
    @Query("SELECT oi FROM OrderItem oi " +
           "LEFT JOIN FETCH oi.service " +
           "WHERE oi.order.id = :orderId")
    List<OrderItem> findOrderItemsByOrderId(@Param("orderId") Long orderId);
    
    /**
     * Lấy promotions của một order
     * @param orderId ID của order
     * @return Set<Promotion>
     */
    @Query("SELECT p FROM Promotion p " +
           "INNER JOIN p.orders o " +
           "WHERE o.id = :orderId")
    Set<Promotion> findPromotionsByOrderId(@Param("orderId") Long orderId);
    
    /**
     * Tính tổng doanh thu theo trạng thái
     * @param status Trạng thái đơn hàng
     * @return Tổng doanh thu
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
    Double sumTotalAmountByStatus(@Param("status") OrderStatus status);
    
    /**
     * Đếm số orders theo trạng thái
     * @param status Trạng thái đơn hàng
     * @return Số lượng orders
     */
    long countByStatus(OrderStatus status);
    
    // ========================================
    // PHASE 3: STATISTICS QUERIES
    // ========================================
    
    /**
     * Tính trung bình giá trị đơn hàng
     * @return Giá trị trung bình
     */
    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.status != 'CANCELLED'")
    Double getAverageOrderValue();
    
    /**
     * Tìm top customers theo số lượng orders
     * @param limit Số lượng top customers
     * @return List of [userId, orderCount]
     */
    @Query(value = "SELECT user_id, COUNT(*) as order_count FROM orders WHERE status != 'CANCELLED' GROUP BY user_id ORDER BY order_count DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopCustomersByOrderCount(@Param("limit") int limit);
    
    /**
     * Tìm top customers theo tổng giá trị orders
     * @param limit Số lượng top customers
     * @return List of [userId, totalAmount]
     */
    @Query(value = "SELECT user_id, SUM(total_amount) as total_value FROM orders WHERE status != 'CANCELLED' GROUP BY user_id ORDER BY total_value DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopCustomersByTotalValue(@Param("limit") int limit);
    
    /**
     * Tính tổng doanh thu trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Tổng doanh thu
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED'")
    Double sumTotalAmountByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // ========================================
    // PHASE 3: ADVANCED SEARCH QUERIES
    // ========================================
    
    /**
     * Tìm kiếm orders theo nhiều tiêu chí (dynamic query)
     * Sử dụng JPQL với optional parameters
     * LEFT JOIN FETCH để tránh N+1 query problem
     * Note: Không fetch orderItems/promotions để tránh MultipleBagFetchException
     */
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.user " +
           "LEFT JOIN FETCH o.branch " +
           "WHERE " +
           "(:userId IS NULL OR o.user.id = :userId) AND " +
           "(:branchId IS NULL OR o.branch.id = :branchId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:dateFrom IS NULL OR o.orderDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR o.orderDate <= :dateTo) AND " +
           "(:minAmount IS NULL OR o.totalAmount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR o.totalAmount <= :maxAmount)")
    List<Order> searchOrders(
        @Param("userId") Long userId,
        @Param("branchId") Long branchId,
        @Param("status") OrderStatus status,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo,
        @Param("minAmount") Double minAmount,
        @Param("maxAmount") Double maxAmount
    );
    
    // ========================================
    // SOFT DELETE METHODS
    // ========================================
    
    @Query(value = "SELECT * FROM orders WHERE deleted_at IS NOT NULL", nativeQuery = true)
    List<Order> findAllDeleted();
    
    @Query(value = "SELECT * FROM orders WHERE id = :id AND deleted_at IS NOT NULL", nativeQuery = true)
    Optional<Order> findDeletedById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE orders SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    int restoreById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM orders WHERE id = :id", nativeQuery = true)
    int permanentlyDeleteById(@Param("id") Long id);
}
