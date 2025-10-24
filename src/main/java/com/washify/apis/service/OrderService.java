package com.washify.apis.service;

import com.washify.apis.annotation.Audited;
import com.washify.apis.dto.request.OrderItemRequest;
import com.washify.apis.dto.request.OrderRequest;
import com.washify.apis.dto.response.OrderItemResponse;
import com.washify.apis.dto.response.OrderResponse;
import com.washify.apis.entity.*;
import com.washify.apis.enums.OrderStatus;
import com.washify.apis.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Order
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final ServiceRepository serviceRepository;
    private final PromotionRepository promotionRepository;
    private final GuestUserService guestUserService;
    
    /**
     * Tạo đơn hàng mới
     * - CUSTOMER tự đặt: truyền userId
     * - STAFF tạo cho khách: truyền phoneNumber (tự động tạo/tìm user)
     */
    @Audited(action = "CREATE_ORDER", entityType = "Order", description = "Tạo đơn hàng mới")
    public OrderResponse createOrder(OrderRequest request) {
        User user;
        
        // Xác định user: từ userId hoặc phoneNumber
        if (request.getUserId() != null) {
            // Case 1: Customer tự đặt (có userId từ JWT)
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));
        } else if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            // Case 2: Staff tạo cho khách (có phoneNumber)
            // Tự động tìm hoặc tạo GUEST user
            user = guestUserService.findOrCreateUserByPhone(request.getPhoneNumber());
        } else {
            throw new RuntimeException("Phải cung cấp userId hoặc phoneNumber");
        }
        
        // Tạo order
        Order order = new Order();
        order.setUser(user);
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);
        
        // Generate order code (WF + YYYYMMDD + incrementing number)
        // Will be set after save to get the ID
        
        // Set branch nếu có
        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh với ID: " + request.getBranchId()));
            order.setBranch(branch);
        }
        
        // Tạo order items
        Set<OrderItem> orderItems = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderItemRequest itemRequest : request.getItems()) {
            com.washify.apis.entity.Service service = serviceRepository.findById(itemRequest.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + itemRequest.getServiceId()));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setService(service);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(service.getPrice());
            
            orderItems.add(orderItem);
            
            // Tính tổng tiền
            BigDecimal itemTotal = service.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        
        order.setOrderItems(orderItems);
        
        // Áp dụng khuyến mãi nếu có
        if (request.getPromotionCodes() != null && !request.getPromotionCodes().isEmpty()) {
            Set<Promotion> promotions = new HashSet<>();
            for (String code : request.getPromotionCodes()) {
                Promotion promotion = promotionRepository.findByCode(code)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy mã khuyến mãi: " + code));
                
                // Tính giảm giá
                if (promotion.getDiscountType() == Promotion.DiscountType.PERCENT) {
                    BigDecimal discount = totalAmount.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100));
                    totalAmount = totalAmount.subtract(discount);
                } else {
                    totalAmount = totalAmount.subtract(promotion.getDiscountValue());
                }
                
                promotions.add(promotion);
            }
            order.setPromotions(promotions);
        }
        
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        
        // Generate and update order code after save (to get ID)
        String orderCode = generateOrderCode(savedOrder.getId(), savedOrder.getOrderDate());
        savedOrder.setOrderCode(orderCode);
        savedOrder = orderRepository.save(savedOrder);
        
        return mapToOrderResponse(savedOrder);
    }
    
    /**
     * Generate order code: WF + YYYYMMDD + ID (padded to 4 digits)
     * Example: WF202510210001
     */
    private String generateOrderCode(Long orderId, java.time.LocalDateTime orderDate) {
        String dateStr = orderDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String idStr = String.format("%04d", orderId);
        return "WF" + dateStr + idStr;
    }
    
    /**
     * Lấy thông tin đơn hàng theo ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        // Safely load collections using separate queries
        return mapToOrderResponseWithCollections(order);
    }
    
    /**
     * Lấy danh sách đơn hàng của user
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToOrderResponseSimple)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách đơn hàng theo trạng thái
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(String status) {
        // Map old status values to new ones for backward compatibility
        String mappedStatus = mapLegacyStatus(status);
        
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(mappedStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status + ". Các trạng thái hợp lệ: PENDING, CONFIRMED, PROCESSING, READY, DELIVERING, COMPLETED, CANCELLED, REFUNDED");
        }
        
        return orderRepository.findByStatus(orderStatus).stream()
                .map(this::mapToOrderResponseSimple)
                .collect(Collectors.toList());
    }
    
    /**
     * Map legacy status values to new enum values for backward compatibility
     */
    private String mapLegacyStatus(String status) {
        if ("IN_PROGRESS".equalsIgnoreCase(status)) {
            return "PROCESSING";
        }
        return status.toUpperCase();
    }
    
    /**
     * Cập nhật trạng thái đơn hàng
     */
    @Audited(action = "UPDATE_ORDER_STATUS", entityType = "Order", description = "Cập nhật trạng thái đơn hàng")
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        // Map old status values to new ones for backward compatibility
        String mappedStatus = mapLegacyStatus(status);
        
        try {
            order.setStatus(OrderStatus.valueOf(mappedStatus));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status + ". Các trạng thái hợp lệ: PENDING, CONFIRMED, PROCESSING, READY, DELIVERING, COMPLETED, CANCELLED, REFUNDED");
        }
        
        Order updatedOrder = orderRepository.save(order);
        
        return mapToOrderResponse(updatedOrder);
    }
    
    /**
     * Hủy đơn hàng
     */
    @Audited(action = "CANCEL_ORDER", entityType = "Order", description = "Hủy đơn hàng")
    public OrderResponse cancelOrder(Long orderId) {
        return updateOrderStatus(orderId, "CANCELLED");
    }
    
    /**
     * Map Entity sang DTO Response với safe collection loading
     * Used for getOrderById to avoid StackOverflowError
     */
    private OrderResponse mapToOrderResponseWithCollections(Order order) {
        // Load collections using separate queries to avoid circular references
        List<OrderItemResponse> itemResponses = new java.util.ArrayList<>();
        List<String> promotionCodes = new java.util.ArrayList<>();
        
        try {
            // Use separate repository query to load order items safely
            List<OrderItem> orderItems = orderRepository.findOrderItemsByOrderId(order.getId());
            itemResponses = orderItems.stream()
                    .map(this::mapToOrderItemResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // If loading fails, use empty list
            itemResponses = new java.util.ArrayList<>();
        }
        
        try {
            // Use separate repository query to load promotions safely
            Set<Promotion> promotions = orderRepository.findPromotionsByOrderId(order.getId());
            promotionCodes = promotions.stream()
                    .map(Promotion::getCode)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // If loading fails, use empty list
            promotionCodes = new java.util.ArrayList<>();
        }
        
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFullName())
                .branchId(order.getBranch() != null ? order.getBranch().getId() : null)
                .branchName(order.getBranch() != null ? order.getBranch().getName() : null)
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .items(itemResponses)
                .promotionCodes(promotionCodes)
                .build();
    }
    
    /**
     * Map Entity sang DTO Response
     */
    private OrderResponse mapToOrderResponse(Order order) {
        // Safely access collections to avoid lazy loading issues
        List<OrderItemResponse> itemResponses = new java.util.ArrayList<>();
        List<String> promotionCodes = new java.util.ArrayList<>();
        
        try {
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                itemResponses = order.getOrderItems().stream()
                        .map(this::mapToOrderItemResponse)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // If lazy loading fails, use empty list
            itemResponses = new java.util.ArrayList<>();
        }
        
        try {
            if (order.getPromotions() != null && !order.getPromotions().isEmpty()) {
                promotionCodes = order.getPromotions().stream()
                        .map(Promotion::getCode)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // If lazy loading fails, use empty list
            promotionCodes = new java.util.ArrayList<>();
        }
        
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFullName())
                .branchId(order.getBranch() != null ? order.getBranch().getId() : null)
                .branchName(order.getBranch() != null ? order.getBranch().getName() : null)
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .items(itemResponses)
                .promotionCodes(promotionCodes)
                .build();
    }
    
    /**
     * Map Entity sang DTO Response (Simple - no lazy collections)
     * Used for search/list operations to avoid StackOverflowError
     */
    private OrderResponse mapToOrderResponseSimple(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFullName())
                .branchId(order.getBranch() != null ? order.getBranch().getId() : null)
                .branchName(order.getBranch() != null ? order.getBranch().getName() : null)
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .items(new java.util.ArrayList<>())  // Empty list - avoid lazy loading
                .promotionCodes(new java.util.ArrayList<>())  // Empty list - avoid lazy loading
                .build();
    }
    
    /**
     * Map OrderItem Entity sang DTO Response
     */
    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        BigDecimal subtotal = orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
        
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .serviceId(orderItem.getService().getId())
                .serviceName(orderItem.getService().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .subtotal(subtotal)
                .build();
    }
    
    // ========================================
    // ENHANCEMENTS - Phase 2
    // ========================================
    
    /**
     * Áp dụng mã khuyến mãi cho đơn hàng
     */
    public OrderResponse applyPromotion(Long orderId, String promotionCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        // Kiểm tra trạng thái đơn hàng
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể áp dụng khuyến mãi cho đơn hàng đang chờ xử lý");
        }
        
        Promotion promotion = promotionRepository.findByCode(promotionCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã khuyến mãi: " + promotionCode));
        
        // Validate promotion
        if (!promotion.getIsActive()) {
            throw new RuntimeException("Mã khuyến mãi không còn hoạt động");
        }
        
        if (promotion.getStartDate() != null && promotion.getStartDate().isAfter(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Mã khuyến mãi chưa có hiệu lực");
        }
        
        if (promotion.getEndDate() != null && promotion.getEndDate().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Mã khuyến mãi đã hết hạn");
        }
        
        // Tính lại tổng tiền
        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Áp dụng giảm giá
        if (promotion.getDiscountType() == Promotion.DiscountType.PERCENT) {
            BigDecimal discount = totalAmount.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100));
            totalAmount = totalAmount.subtract(discount);
        } else {
            totalAmount = totalAmount.subtract(promotion.getDiscountValue());
        }
        
        // Thêm promotion vào order
        order.getPromotions().add(promotion);
        order.setTotalAmount(totalAmount);
        
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }
    
    /**
     * Xóa mã khuyến mãi khỏi đơn hàng
     */
    public OrderResponse removePromotion(Long orderId, String promotionCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể xóa khuyến mãi cho đơn hàng đang chờ xử lý");
        }
        
        Promotion promotion = promotionRepository.findByCode(promotionCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã khuyến mãi: " + promotionCode));
        
        // Xóa promotion
        order.getPromotions().remove(promotion);
        
        // Tính lại tổng tiền
        recalculateOrderTotal(order);
        
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }
    
    /**
     * Lấy danh sách tất cả orders (Admin/Staff)
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponseSimple)
                .collect(Collectors.toList());
    }
    
    /**
     * Tính lại tổng tiền đơn hàng với các promotions hiện tại
     */
    private void recalculateOrderTotal(Order order) {
        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Áp dụng các promotions còn lại
        for (Promotion promotion : order.getPromotions()) {
            if (promotion.getDiscountType() == Promotion.DiscountType.PERCENT) {
                BigDecimal discount = totalAmount.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100));
                totalAmount = totalAmount.subtract(discount);
            } else {
                totalAmount = totalAmount.subtract(promotion.getDiscountValue());
            }
        }
        
        order.setTotalAmount(totalAmount);
    }
    
    // ========================================
    // PHASE 3: STATISTICS & ANALYTICS
    // ========================================
    
    /**
     * Lấy thống kê tổng quan về orders
     */
    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics() {
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long inProgressOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
        long completedOrders = orderRepository.countByStatus(OrderStatus.COMPLETED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
        
        Double totalRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED);
        Double averageOrderValue = orderRepository.getAverageOrderValue();
        
        return new OrderStatistics(
            totalOrders,
            pendingOrders,
            inProgressOrders,
            completedOrders,
            cancelledOrders,
            totalRevenue != null ? totalRevenue : 0.0,
            averageOrderValue != null ? averageOrderValue : 0.0
        );
    }
    
    /**
     * Lấy thống kê doanh thu theo khoảng thời gian
     */
    @Transactional(readOnly = true)
    public RevenueStatistics getRevenueStatistics(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        Double totalRevenue = orderRepository.sumTotalAmountByDateRange(startDate, endDate);
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        
        long orderCount = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .count();
        
        double averageOrderValue = orderCount > 0 ? (totalRevenue != null ? totalRevenue : 0.0) / orderCount : 0.0;
        
        return new RevenueStatistics(
            totalRevenue != null ? totalRevenue : 0.0,
            orderCount,
            averageOrderValue,
            startDate,
            endDate
        );
    }
    
    /**
     * Lấy danh sách top customers
     */
    @Transactional(readOnly = true)
    public List<TopCustomer> getTopCustomers(int limit) {
        List<Object[]> results = orderRepository.findTopCustomersByOrderCount(limit);
        
        return results.stream().map(row -> {
            Long userId = ((Number) row[0]).longValue();
            Long orderCount = ((Number) row[1]).longValue();
            
            User user = userRepository.findById(userId).orElse(null);
            String username = user != null ? user.getUsername() : "Unknown";
            String fullName = user != null ? user.getFullName() : "Unknown";
            
            // Get total value for this customer
            Double totalValue = orderRepository.findByUserId(userId).stream()
                    .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .doubleValue();
            
            return new TopCustomer(userId, username, fullName, orderCount, totalValue);
        }).collect(Collectors.toList());
    }
    
    /**
     * Inner class cho Order Statistics
     */
    public static class OrderStatistics {
        public final long totalOrders;
        public final long pendingOrders;
        public final long inProgressOrders;
        public final long completedOrders;
        public final long cancelledOrders;
        public final double totalRevenue;
        public final double averageOrderValue;
        
        public OrderStatistics(long totalOrders, long pendingOrders, long inProgressOrders,
                             long completedOrders, long cancelledOrders,
                             double totalRevenue, double averageOrderValue) {
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
            this.inProgressOrders = inProgressOrders;
            this.completedOrders = completedOrders;
            this.cancelledOrders = cancelledOrders;
            this.totalRevenue = totalRevenue;
            this.averageOrderValue = averageOrderValue;
        }
    }
    
    /**
     * Inner class cho Revenue Statistics
     */
    public static class RevenueStatistics {
        public final double totalRevenue;
        public final long orderCount;
        public final double averageOrderValue;
        public final java.time.LocalDateTime startDate;
        public final java.time.LocalDateTime endDate;
        
        public RevenueStatistics(double totalRevenue, long orderCount, double averageOrderValue,
                                java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
            this.totalRevenue = totalRevenue;
            this.orderCount = orderCount;
            this.averageOrderValue = averageOrderValue;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
    
    /**
     * Inner class cho Top Customer
     */
    public static class TopCustomer {
        public final Long userId;
        public final String username;
        public final String fullName;
        public final Long orderCount;
        public final Double totalValue;
        
        public TopCustomer(Long userId, String username, String fullName, Long orderCount, Double totalValue) {
            this.userId = userId;
            this.username = username;
            this.fullName = fullName;
            this.orderCount = orderCount;
            this.totalValue = totalValue;
        }
    }
    
    // ========================================
    // PHASE 3: ADVANCED SEARCH METHODS
    // ========================================
    
    /**
     * Tìm kiếm orders theo nhiều tiêu chí
     */
    public List<OrderResponse> searchOrders(Long userId, Long branchId, String status, 
                                           java.time.LocalDateTime dateFrom, java.time.LocalDateTime dateTo,
                                           Double minAmount, Double maxAmount) {
        // Convert string status to enum
        OrderStatus orderStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                orderStatus = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Trạng thái không hợp lệ: " + status);
            }
        }
        
        List<Order> orders = orderRepository.searchOrders(
            userId, branchId, orderStatus, dateFrom, dateTo, minAmount, maxAmount
        );
        
        // Map to response without accessing lazy-loaded collections
        return orders.stream()
                .map(this::mapToOrderResponseSimple)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy orders của user theo status
     */
    public List<OrderResponse> getOrdersByUserAndStatus(Long userId, String status) {
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }
        
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, orderStatus);
        return orders.stream()
                .map(this::mapToOrderResponseSimple)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy orders theo branch
     */
    public List<OrderResponse> getOrdersByBranch(Long branchId) {
        // Validate branch exists
        branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh với ID: " + branchId));
        
        List<Order> orders = orderRepository.findByBranchId(branchId);
        return orders.stream()
                .map(this::mapToOrderResponseSimple)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy orders theo khoảng thời gian
     */
    public List<OrderResponse> getOrdersByDateRange(java.time.LocalDateTime startDate, 
                                                    java.time.LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc");
        }
        
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        return orders.stream()
                .map(this::mapToOrderResponseSimple)
                .collect(Collectors.toList());
    }
    
    // ========================================
    // BATCH OPERATIONS
    // ========================================
    
    /**
     * Cập nhật status cho nhiều orders cùng lúc
     */
    public int batchUpdateStatus(List<Long> orderIds, String status) {
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }
        
        int count = 0;
        for (Long orderId : orderIds) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.setStatus(orderStatus);
                orderRepository.save(order);
                count++;
            }
        }
        return count;
    }
    
    /**
     * Hủy nhiều orders cùng lúc
     */
    public int batchCancelOrders(List<Long> orderIds) {
        int count = 0;
        for (Long orderId : orderIds) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null && order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                count++;
            }
        }
        return count;
    }
}
