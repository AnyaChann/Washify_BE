package com.washify.apis.controller;

import com.washify.apis.dto.request.OrderRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.OrderResponse;
import com.washify.apis.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến Order
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "📦 Orders", description = "Quản lý đơn hàng - 👤 Customer/Staff/Admin")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * Tạo đơn hàng mới
     * POST /api/orders
     * Customer tạo đơn cho mình, Staff tạo cho khách
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Tạo đơn hàng thành công"));
    }
    
    /**
     * Lấy thông tin đơn hàng theo ID
     * GET /api/orders/{id}
     * Admin/Staff xem tất cả, Customer xem đơn của mình
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order, "Lấy thông tin đơn hàng thành công"));
    }
    
    /**
     * Lấy danh sách đơn hàng của user
     * GET /api/orders/user/{userId}
     * Admin/Staff xem tất cả, User xem của mình
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(orders, "Lấy danh sách đơn hàng thành công"));
    }
    
    /**
     * Lấy danh sách đơn hàng theo trạng thái
     * GET /api/orders/status/{status}
     * Chỉ Admin và Staff
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByStatus(@PathVariable String status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(orders, "Lấy danh sách đơn hàng thành công"));
    }
    
    /**
     * Cập nhật trạng thái đơn hàng
     * PATCH /api/orders/{id}/status
     * Chỉ Staff và Admin
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        OrderResponse order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(order, "Cập nhật trạng thái đơn hàng thành công"));
    }
    
    /**
     * Hủy đơn hàng
     * PATCH /api/orders/{id}/cancel
     * Customer hủy đơn của mình, Staff/Admin hủy bất kỳ đơn nào
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        OrderResponse order = orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success(order, "Hủy đơn hàng thành công"));
    }
    
    // ========================================
    // ENHANCEMENTS - Phase 2
    // ========================================
    
    /**
     * Lấy tất cả đơn hàng
     * GET /api/orders
     * Chỉ Admin và Staff
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders, "Lấy danh sách đơn hàng thành công"));
    }
    
    /**
     * Áp dụng mã khuyến mãi cho đơn hàng
     * POST /api/orders/{id}/promotions
     * Customer áp dụng cho đơn của mình, Staff/Admin áp dụng cho bất kỳ đơn nào
     */
    @PostMapping("/{id}/promotions")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> applyPromotion(
            @PathVariable Long id,
            @RequestParam String code) {
        OrderResponse order = orderService.applyPromotion(id, code);
        return ResponseEntity.ok(ApiResponse.success(order, "Áp dụng mã khuyến mãi thành công"));
    }
    
    /**
     * Xóa mã khuyến mãi khỏi đơn hàng
     * DELETE /api/orders/{id}/promotions
     * Customer xóa khỏi đơn của mình, Staff/Admin xóa khỏi bất kỳ đơn nào
     */
    @DeleteMapping("/{id}/promotions")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> removePromotion(
            @PathVariable Long id,
            @RequestParam String code) {
        OrderResponse order = orderService.removePromotion(id, code);
        return ResponseEntity.ok(ApiResponse.success(order, "Xóa mã khuyến mãi thành công"));
    }
    
    // ========================================
    // PHASE 3: STATISTICS & ANALYTICS
    // ========================================
    
    /**
     * Lấy thống kê tổng quan về orders
     * GET /api/orders/statistics
     * Admin, Staff, Manager
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Lấy thống kê orders",
        description = "Thống kê tổng quan: tổng số orders, theo status, doanh thu, giá trị trung bình. ADMIN, STAFF và MANAGER."
    )
    public ResponseEntity<ApiResponse<OrderService.OrderStatistics>> getOrderStatistics() {
        OrderService.OrderStatistics stats = orderService.getOrderStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê orders thành công"));
    }
    
    /**
     * Lấy thống kê doanh thu theo khoảng thời gian
     * GET /api/orders/statistics/revenue
     * Admin, Staff, Manager
     */
    @GetMapping("/statistics/revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Thống kê doanh thu theo thời gian",
        description = "Doanh thu, số lượng orders, giá trị TB trong khoảng thời gian. Format: yyyy-MM-dd'T'HH:mm:ss. ADMIN, STAFF và MANAGER."
    )
    public ResponseEntity<ApiResponse<OrderService.RevenueStatistics>> getRevenueStatistics(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) 
            java.time.LocalDateTime startDate,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) 
            java.time.LocalDateTime endDate) {
        OrderService.RevenueStatistics stats = orderService.getRevenueStatistics(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê doanh thu thành công"));
    }
    
    /**
     * Lấy danh sách top customers
     * GET /api/orders/statistics/top-customers
     * Admin, Staff, Manager
     */
    @GetMapping("/statistics/top-customers")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Top customers theo số lượng orders",
        description = "Danh sách top customers với số lượng orders và tổng giá trị. ADMIN, STAFF và MANAGER."
    )
    public ResponseEntity<ApiResponse<List<OrderService.TopCustomer>>> getTopCustomers(
            @RequestParam(defaultValue = "10") int limit) {
        List<OrderService.TopCustomer> topCustomers = orderService.getTopCustomers(limit);
        return ResponseEntity.ok(ApiResponse.success(topCustomers, "Lấy top customers thành công"));
    }
    
    // ========================================
    // PHASE 3: ADVANCED SEARCH & FILTERING
    // ========================================
    
    /**
     * Tìm kiếm orders theo nhiều tiêu chí
     * GET /api/orders/search
     * Admin, Staff, Manager
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Tìm kiếm orders theo nhiều tiêu chí",
        description = "Search với userId, branchId, status, dateFrom, dateTo, minAmount, maxAmount. ADMIN, STAFF và MANAGER."
    )
    public ResponseEntity<ApiResponse<List<OrderResponse>>> searchOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dateFrom,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dateTo,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount) {
        List<OrderResponse> orders = orderService.searchOrders(userId, branchId, status, dateFrom, dateTo, minAmount, maxAmount);
        return ResponseEntity.ok(ApiResponse.success(orders, "Tìm kiếm orders thành công"));
    }
    
    /**
     * Lấy orders của user theo status
     * GET /api/orders/user/{userId}/status/{status}
     * Admin/Staff xem tất cả, User xem của mình
     */
    @GetMapping("/user/{userId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER') or #userId == authentication.principal.id")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Orders của user theo status",
        description = "Lấy orders của một user cụ thể theo trạng thái."
    )
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByUserAndStatus(
            @PathVariable Long userId,
            @PathVariable String status) {
        List<OrderResponse> orders = orderService.getOrdersByUserAndStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(orders, "Lấy danh sách orders thành công"));
    }
    
    /**
     * Lấy orders theo branch
     * GET /api/orders/branch/{branchId}
     * Admin, Staff, Manager
     */
    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Orders theo chi nhánh",
        description = "Lấy tất cả orders của một chi nhánh. ADMIN, STAFF và MANAGER."
    )
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByBranch(@PathVariable Long branchId) {
        List<OrderResponse> orders = orderService.getOrdersByBranch(branchId);
        return ResponseEntity.ok(ApiResponse.success(orders, "Lấy danh sách orders thành công"));
    }
    
    /**
     * Lấy orders theo khoảng thời gian
     * GET /api/orders/date-range
     * Admin, Staff, Manager
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Orders theo khoảng thời gian",
        description = "Lấy orders trong khoảng thời gian. Format: yyyy-MM-dd'T'HH:mm:ss. ADMIN, STAFF và MANAGER."
    )
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByDateRange(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate) {
        List<OrderResponse> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(orders, "Lấy danh sách orders thành công"));
    }
    
    // ========================================
    // BATCH OPERATIONS
    // ========================================
    
    /**
     * Cập nhật status cho nhiều orders cùng lúc
     * PATCH /api/orders/batch/status
     * Admin, Staff, Manager
     */
    @PatchMapping("/batch/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Cập nhật status nhiều orders",
        description = "Cập nhật status cho nhiều orders cùng lúc. Chỉ ADMIN và STAFF."
    )
    public ResponseEntity<ApiResponse<Integer>> batchUpdateStatus(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Danh sách order IDs và status mới") 
            java.util.Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> orderIds = (List<Long>) request.get("orderIds");
        String status = (String) request.get("status");
        
        int updatedCount = orderService.batchUpdateStatus(orderIds, status);
        return ResponseEntity.ok(ApiResponse.success(updatedCount, 
            "Cập nhật status cho " + updatedCount + " orders thành công"));
    }
    
    /**
     * Hủy nhiều orders cùng lúc
     * DELETE /api/orders/batch
     * Chỉ Admin và Staff
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Hủy nhiều orders",
        description = "Hủy nhiều orders cùng lúc (set status = CANCELLED). Chỉ ADMIN và STAFF."
    )
    public ResponseEntity<ApiResponse<Integer>> batchCancelOrders(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Danh sách order IDs cần hủy") 
            java.util.Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> orderIds = (List<Long>) request.get("orderIds");
        
        int cancelledCount = orderService.batchCancelOrders(orderIds);
        return ResponseEntity.ok(ApiResponse.success(cancelledCount, 
            "Hủy " + cancelledCount + " orders thành công"));
    }
}
