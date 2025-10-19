package com.washify.apis.controller;

import com.washify.apis.dto.request.PaymentRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.PaymentResponse;
import com.washify.apis.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến Payment
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * Tạo thanh toán mới
     * POST /api/payments
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(payment, "Tạo thanh toán thành công"));
    }
    
    /**
     * Lấy thông tin thanh toán theo ID
     * GET /api/payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(payment, "Lấy thông tin thanh toán thành công"));
    }
    
    /**
     * Lấy thanh toán theo order ID
     * GET /api/payments/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentResponse payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(payment, "Lấy thông tin thanh toán thành công"));
    }
    
    /**
     * Lấy danh sách thanh toán theo trạng thái
     * GET /api/payments/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByStatus(@PathVariable String status) {
        List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(payments, "Lấy danh sách thanh toán thành công"));
    }
    
    /**
     * Cập nhật trạng thái thanh toán
     * PATCH /api/payments/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        PaymentResponse payment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(payment, "Cập nhật trạng thái thanh toán thành công"));
    }
    
    /**
     * Xác nhận thanh toán thành công
     * PATCH /api/payments/{id}/confirm
     */
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.confirmPayment(id);
        return ResponseEntity.ok(ApiResponse.success(payment, "Xác nhận thanh toán thành công"));
    }
    
    /**
     * Đánh dấu thanh toán thất bại
     * PATCH /api/payments/{id}/fail
     */
    @PatchMapping("/{id}/fail")
    public ResponseEntity<ApiResponse<PaymentResponse>> failPayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.failPayment(id);
        return ResponseEntity.ok(ApiResponse.success(payment, "Đánh dấu thanh toán thất bại"));
    }
}
