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
}
