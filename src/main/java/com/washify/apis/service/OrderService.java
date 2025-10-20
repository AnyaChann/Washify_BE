package com.washify.apis.service;

import com.washify.apis.dto.request.OrderItemRequest;
import com.washify.apis.dto.request.OrderRequest;
import com.washify.apis.dto.response.OrderItemResponse;
import com.washify.apis.dto.response.OrderResponse;
import com.washify.apis.entity.*;
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
    
    /**
     * Tạo đơn hàng mới
     */
    public OrderResponse createOrder(OrderRequest request) {
        // Tìm user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));
        
        // Tạo order
        Order order = new Order();
        order.setUser(user);
        order.setNotes(request.getNotes());
        order.setStatus(Order.OrderStatus.PENDING);
        
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
        
        return mapToOrderResponse(savedOrder);
    }
    
    /**
     * Lấy thông tin đơn hàng theo ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        return mapToOrderResponse(order);
    }
    
    /**
     * Lấy danh sách đơn hàng của user
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách đơn hàng theo trạng thái
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
        return orderRepository.findByStatus(orderStatus).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật trạng thái đơn hàng
     */
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        order.setStatus(Order.OrderStatus.valueOf(status));
        Order updatedOrder = orderRepository.save(order);
        
        return mapToOrderResponse(updatedOrder);
    }
    
    /**
     * Hủy đơn hàng
     */
    public OrderResponse cancelOrder(Long orderId) {
        return updateOrderStatus(orderId, "CANCELLED");
    }
    
    /**
     * Map Entity sang DTO Response
     */
    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFullName())
                .branchId(order.getBranch() != null ? order.getBranch().getId() : null)
                .branchName(order.getBranch() != null ? order.getBranch().getName() : null)
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .items(order.getOrderItems().stream()
                        .map(this::mapToOrderItemResponse)
                        .collect(Collectors.toList()))
                .promotionCodes(order.getPromotions().stream()
                        .map(Promotion::getCode)
                        .collect(Collectors.toList()))
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
        if (order.getStatus() != Order.OrderStatus.PENDING) {
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
        
        if (order.getStatus() != Order.OrderStatus.PENDING) {
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
                .map(this::mapToOrderResponse)
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
}
