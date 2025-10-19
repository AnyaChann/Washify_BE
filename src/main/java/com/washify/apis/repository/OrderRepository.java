package com.washify.apis.repository;

import com.washify.apis.entity.Order;
import com.washify.apis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface cho Order entity
 * Cung cấp các phương thức truy vấn database cho bảng orders
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
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * Tìm orders của user theo trạng thái
     * @param userId ID của user
     * @param status Trạng thái đơn hàng
     * @return Danh sách orders
     */
    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);
    
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
     * Tính tổng doanh thu theo trạng thái
     * @param status Trạng thái đơn hàng
     * @return Tổng doanh thu
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
    Double sumTotalAmountByStatus(@Param("status") Order.OrderStatus status);
    
    /**
     * Đếm số orders theo trạng thái
     * @param status Trạng thái đơn hàng
     * @return Số lượng orders
     */
    long countByStatus(Order.OrderStatus status);
}
