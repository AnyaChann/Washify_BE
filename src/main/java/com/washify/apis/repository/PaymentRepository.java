package com.washify.apis.repository;

import com.washify.apis.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Payment entity
 * Cung cấp các phương thức truy vấn database cho bảng payments
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Tìm payment theo order ID
     * @param orderId ID của order
     * @return Optional chứa Payment nếu tìm thấy
     */
    Optional<Payment> findByOrderId(Long orderId);
    
    /**
     * Tìm payments theo trạng thái
     * @param paymentStatus Trạng thái thanh toán
     * @return Danh sách payments
     */
    List<Payment> findByPaymentStatus(Payment.PaymentStatus paymentStatus);
    
    /**
     * Tìm payments theo phương thức thanh toán
     * @param paymentMethod Phương thức thanh toán
     * @return Danh sách payments
     */
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    /**
     * Tìm payments trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách payments
     */
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Tính tổng doanh thu từ payments đã thanh toán
     * @param paymentStatus Trạng thái thanh toán
     * @return Tổng doanh thu
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = :paymentStatus")
    Double sumAmountByPaymentStatus(Payment.PaymentStatus paymentStatus);
}
