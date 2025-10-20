package com.washify.apis.controller;

import com.washify.apis.dto.request.PaymentRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.PaymentResponse;
import com.washify.apis.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến Payment
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "💳 Payments", description = "Quản lý thanh toán - 👤 Customer/Staff/Admin")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * Tạo thanh toán mới
     * POST /api/payments
     * Customer thanh toán đơn của mình
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(payment, "Tạo thanh toán thành công"));
    }
    
    /**
     * Lấy thông tin thanh toán theo ID
     * GET /api/payments/{id}
     * Admin/Staff xem tất cả, Customer xem của mình
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(payment, "Lấy thông tin thanh toán thành công"));
    }
    
    /**
     * Lấy thanh toán theo order ID
     * GET /api/payments/order/{orderId}
     * Admin/Staff xem tất cả, Customer xem của mình
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentResponse payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(payment, "Lấy thông tin thanh toán thành công"));
    }
    
    /**
     * Lấy danh sách thanh toán theo trạng thái
     * GET /api/payments/status/{status}
     * Chỉ Admin và Staff
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByStatus(@PathVariable String status) {
        List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(payments, "Lấy danh sách thanh toán thành công"));
    }
    
    /**
     * Cập nhật trạng thái thanh toán
     * PATCH /api/payments/{id}/status
     * Chỉ Staff và Admin
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        PaymentResponse payment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(payment, "Cập nhật trạng thái thanh toán thành công"));
    }
    
    /**
     * Xác nhận thanh toán thành công
     * PATCH /api/payments/{id}/confirm
     * Chỉ Staff và Admin
     */
    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.confirmPayment(id);
        return ResponseEntity.ok(ApiResponse.success(payment, "Xác nhận thanh toán thành công"));
    }
    
    /**
     * Đánh dấu thanh toán thất bại
     * PATCH /api/payments/{id}/fail
     * Chỉ Staff và Admin
     */
    @PatchMapping("/{id}/fail")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> failPayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.failPayment(id);
        return ResponseEntity.ok(ApiResponse.success(payment, "Đánh dấu thanh toán thất bại"));
    }
    
    // ========================================
    // ENHANCEMENTS - Phase 2
    // ========================================
    
    /**
     * Hoàn tiền (refund)
     * POST /api/payments/{id}/refund
     * Chỉ Admin
     */
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @PathVariable Long id,
            @RequestParam String reason) {
        PaymentResponse payment = paymentService.refundPayment(id, reason);
        return ResponseEntity.ok(ApiResponse.success(payment, "Hoàn tiền thành công"));
    }
    
    /**
     * Webhook từ payment gateway (VD: Momo, ZaloPay, VNPay)
     * POST /api/payments/webhook
     * Public endpoint - không cần authentication (sử dụng signature verification thực tế)
     */
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<PaymentResponse>> handleWebhook(
            @RequestParam Long paymentId,
            @RequestParam String status,
            @RequestParam(required = false) String transactionId) {
        PaymentResponse payment = paymentService.processWebhook(paymentId, status, transactionId);
        return ResponseEntity.ok(ApiResponse.success(payment, "Webhook xử lý thành công"));
    }
    
    /**
     * Lấy thống kê doanh thu
     * GET /api/payments/statistics
     * Chỉ Admin và Staff
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<PaymentService.PaymentStatistics>> getPaymentStatistics() {
        PaymentService.PaymentStatistics stats = paymentService.getPaymentStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê thanh toán thành công"));
    }
    
    /**
     * Lấy payments theo phương thức thanh toán
     * GET /api/payments/method/{method}
     * Admin, Staff, Manager
     */
    @GetMapping("/method/{method}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByMethod(@PathVariable String method) {
        List<PaymentResponse> payments = paymentService.getPaymentsByMethod(method);
        return ResponseEntity.ok(ApiResponse.success(payments, "Lấy danh sách thanh toán thành công"));
    }
    
    /**
     * Lấy payments trong khoảng thời gian
     * GET /api/payments/date-range
     * Admin, Staff, Manager
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByDateRange(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) 
            java.time.LocalDateTime startDate,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) 
            java.time.LocalDateTime endDate) {
        List<PaymentResponse> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(payments, "Lấy danh sách thanh toán thành công"));
    }
}
