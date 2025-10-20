package com.washify.apis.service;

import com.washify.apis.dto.request.PaymentRequest;
import com.washify.apis.dto.response.PaymentResponse;
import com.washify.apis.entity.Order;
import com.washify.apis.entity.Payment;
import com.washify.apis.repository.OrderRepository;
import com.washify.apis.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Payment
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    
    /**
     * Tạo thanh toán mới
     */
    public PaymentResponse createPayment(PaymentRequest request) {
        // Tìm order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + request.getOrderId()));
        
        // Kiểm tra xem order đã có payment chưa
        if (paymentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new RuntimeException("Đơn hàng này đã có thanh toán");
        }
        
        // Tạo payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()));
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        payment.setAmount(request.getAmount());
        
        Payment savedPayment = paymentRepository.save(payment);
        return mapToPaymentResponse(savedPayment);
    }
    
    /**
     * Lấy thông tin thanh toán theo ID
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán với ID: " + paymentId));
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Lấy thanh toán theo order ID
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán cho đơn hàng ID: " + orderId));
        return mapToPaymentResponse(payment);
    }
    
    /**
     * Lấy danh sách thanh toán theo trạng thái
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(String status) {
        Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
        return paymentRepository.findByPaymentStatus(paymentStatus).stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật trạng thái thanh toán
     */
    public PaymentResponse updatePaymentStatus(Long paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán với ID: " + paymentId));
        
        payment.setPaymentStatus(Payment.PaymentStatus.valueOf(status));
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Nếu thanh toán thành công, cập nhật trạng thái order
        if (status.equals("PAID")) {
            Order order = payment.getOrder();
            order.setStatus(Order.OrderStatus.IN_PROGRESS);
            orderRepository.save(order);
        }
        
        return mapToPaymentResponse(updatedPayment);
    }
    
    /**
     * Xác nhận thanh toán thành công
     */
    public PaymentResponse confirmPayment(Long paymentId) {
        return updatePaymentStatus(paymentId, "PAID");
    }
    
    /**
     * Đánh dấu thanh toán thất bại
     */
    public PaymentResponse failPayment(Long paymentId) {
        return updatePaymentStatus(paymentId, "FAILED");
    }
    
    /**
     * Map Entity sang DTO Response
     */
    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .paymentMethod(payment.getPaymentMethod().name())
                .paymentStatus(payment.getPaymentStatus().name())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .build();
    }
    
    // ========================================
    // ENHANCEMENTS - Phase 2
    // ========================================
    
    /**
     * Hoàn tiền (refund)
     */
    public PaymentResponse refundPayment(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán với ID: " + paymentId));
        
        // Chỉ hoàn tiền cho payments đã PAID
        if (payment.getPaymentStatus() != Payment.PaymentStatus.PAID) {
            throw new RuntimeException("Chỉ có thể hoàn tiền cho thanh toán đã thành công");
        }
        
        // Cập nhật order status về CANCELLED
        Order order = payment.getOrder();
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setNotes(order.getNotes() + " | REFUND: " + reason);
        orderRepository.save(order);
        
        // Cập nhật payment status về FAILED (hoặc có thể tạo enum mới REFUNDED)
        payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        Payment updatedPayment = paymentRepository.save(payment);
        
        return mapToPaymentResponse(updatedPayment);
    }
    
    /**
     * Webhook handler (giả lập - thực tế cần implement theo payment gateway cụ thể)
     */
    public PaymentResponse processWebhook(Long paymentId, String status, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán với ID: " + paymentId));
        
        // Xử lý dựa trên status từ webhook
        if ("SUCCESS".equalsIgnoreCase(status) || "PAID".equalsIgnoreCase(status)) {
            payment.setPaymentStatus(Payment.PaymentStatus.PAID);
            
            // Cập nhật order status
            Order order = payment.getOrder();
            order.setStatus(Order.OrderStatus.IN_PROGRESS);
            orderRepository.save(order);
        } else if ("FAILED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        return mapToPaymentResponse(updatedPayment);
    }
    
    /**
     * Lấy payments theo phương thức thanh toán
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByMethod(String method) {
        Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(method);
        return paymentRepository.findByPaymentMethod(paymentMethod).stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy payments trong khoảng thời gian
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByDateRange(
            java.time.LocalDateTime startDate, 
            java.time.LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate).stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Thống kê doanh thu
     */
    @Transactional(readOnly = true)
    public PaymentStatistics getPaymentStatistics() {
        // Tổng doanh thu từ payments đã thanh toán
        Double totalRevenue = paymentRepository.sumAmountByPaymentStatus(Payment.PaymentStatus.PAID);
        
        // Số lượng payments theo status
        long totalPaid = paymentRepository.findByPaymentStatus(Payment.PaymentStatus.PAID).size();
        long totalPending = paymentRepository.findByPaymentStatus(Payment.PaymentStatus.PENDING).size();
        long totalFailed = paymentRepository.findByPaymentStatus(Payment.PaymentStatus.FAILED).size();
        
        // Số lượng theo method
        long cashCount = paymentRepository.findByPaymentMethod(Payment.PaymentMethod.CASH).size();
        long cardCount = paymentRepository.findByPaymentMethod(Payment.PaymentMethod.CARD).size();
        long onlineCount = paymentRepository.findByPaymentMethod(Payment.PaymentMethod.ONLINE).size();
        
        return new PaymentStatistics(
            totalRevenue != null ? totalRevenue : 0.0,
            totalPaid,
            totalPending,
            totalFailed,
            cashCount,
            cardCount,
            onlineCount
        );
    }
    
    /**
     * Inner class cho payment statistics
     */
    public static class PaymentStatistics {
        public final double totalRevenue;
        public final long totalPaid;
        public final long totalPending;
        public final long totalFailed;
        public final long cashPayments;
        public final long cardPayments;
        public final long onlinePayments;
        
        public PaymentStatistics(double totalRevenue, long totalPaid, long totalPending, 
                               long totalFailed, long cashPayments, long cardPayments, 
                               long onlinePayments) {
            this.totalRevenue = totalRevenue;
            this.totalPaid = totalPaid;
            this.totalPending = totalPending;
            this.totalFailed = totalFailed;
            this.cashPayments = cashPayments;
            this.cardPayments = cardPayments;
            this.onlinePayments = onlinePayments;
        }
    }
}
