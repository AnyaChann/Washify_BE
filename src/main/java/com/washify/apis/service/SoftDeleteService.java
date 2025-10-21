package com.washify.apis.service;

import com.washify.apis.annotation.Audited;
import com.washify.apis.entity.*;
import com.washify.apis.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service xử lý các operations liên quan đến Soft Delete
 * Bao gồm: restore (khôi phục), lấy danh sách đã xóa, xóa vĩnh viễn
 */
@Service
@RequiredArgsConstructor
public class SoftDeleteService {
    
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final ServiceRepository serviceRepository;
    private final OrderRepository orderRepository;
    private final PromotionRepository promotionRepository;
    private final ShipperRepository shipperRepository;
    
    // ========================================
    // USER SOFT DELETE OPERATIONS
    // ========================================
    
    /**
     * Lấy danh sách users đã bị xóa mềm
     */
    public List<User> getDeletedUsers() {
        return userRepository.findAllDeleted();
    }
    
    /**
     * Khôi phục user đã bị xóa mềm
     */
    @Audited(action = "RESTORE_USER", entityType = "User", description = "Khôi phục user đã xóa mềm")
    @Transactional
    public boolean restoreUser(Long userId) {
        return userRepository.restoreById(userId) > 0;
    }
    
    /**
     * Xóa vĩnh viễn user - CẢNH BÁO: Không thể khôi phục!
     */
    @Audited(action = "PERMANENT_DELETE_USER", entityType = "User", description = "Xóa vĩnh viễn user khỏi database")
    @Transactional
    public boolean permanentlyDeleteUser(Long userId) {
        return userRepository.permanentlyDeleteById(userId) > 0;
    }
    
    // ========================================
    // BRANCH SOFT DELETE OPERATIONS
    // ========================================
    
    public List<Branch> getDeletedBranches() {
        return branchRepository.findAllDeleted();
    }
    
    @Audited(action = "RESTORE_BRANCH", entityType = "Branch", description = "Khôi phục chi nhánh đã xóa mềm")
    @Transactional
    public boolean restoreBranch(Long branchId) {
        return branchRepository.restoreById(branchId) > 0;
    }
    
    @Audited(action = "PERMANENT_DELETE_BRANCH", entityType = "Branch", description = "Xóa vĩnh viễn chi nhánh")
    @Transactional
    public boolean permanentlyDeleteBranch(Long branchId) {
        return branchRepository.permanentlyDeleteById(branchId) > 0;
    }
    
    // ========================================
    // SERVICE SOFT DELETE OPERATIONS
    // ========================================
    
    public List<com.washify.apis.entity.Service> getDeletedServices() {
        return serviceRepository.findAllDeleted();
    }
    
    @Audited(action = "RESTORE_SERVICE", entityType = "Service", description = "Khôi phục dịch vụ đã xóa mềm")
    @Transactional
    public boolean restoreService(Long serviceId) {
        return serviceRepository.restoreById(serviceId) > 0;
    }
    
    @Audited(action = "PERMANENT_DELETE_SERVICE", entityType = "Service", description = "Xóa vĩnh viễn dịch vụ")
    @Transactional
    public boolean permanentlyDeleteService(Long serviceId) {
        return serviceRepository.permanentlyDeleteById(serviceId) > 0;
    }
    
    // ========================================
    // ORDER SOFT DELETE OPERATIONS
    // ========================================
    
    public List<Order> getDeletedOrders() {
        return orderRepository.findAllDeleted();
    }
    
    @Audited(action = "RESTORE_ORDER", entityType = "Order", description = "Khôi phục đơn hàng đã xóa mềm")
    @Transactional
    public boolean restoreOrder(Long orderId) {
        return orderRepository.restoreById(orderId) > 0;
    }
    
    @Audited(action = "PERMANENT_DELETE_ORDER", entityType = "Order", description = "Xóa vĩnh viễn đơn hàng")
    @Transactional
    public boolean permanentlyDeleteOrder(Long orderId) {
        return orderRepository.permanentlyDeleteById(orderId) > 0;
    }
    
    // ========================================
    // PROMOTION SOFT DELETE OPERATIONS
    // ========================================
    
    public List<Promotion> getDeletedPromotions() {
        return promotionRepository.findAllDeleted();
    }
    
    @Audited(action = "RESTORE_PROMOTION", entityType = "Promotion", description = "Khôi phục khuyến mãi đã xóa mềm")
    @Transactional
    public boolean restorePromotion(Long promotionId) {
        return promotionRepository.restoreById(promotionId) > 0;
    }
    
    @Audited(action = "PERMANENT_DELETE_PROMOTION", entityType = "Promotion", description = "Xóa vĩnh viễn khuyến mãi")
    @Transactional
    public boolean permanentlyDeletePromotion(Long promotionId) {
        return promotionRepository.permanentlyDeleteById(promotionId) > 0;
    }
    
    // ========================================
    // SHIPPER SOFT DELETE OPERATIONS
    // ========================================
    
    public List<Shipper> getDeletedShippers() {
        return shipperRepository.findAllDeleted();
    }
    
    @Audited(action = "RESTORE_SHIPPER", entityType = "Shipper", description = "Khôi phục shipper đã xóa mềm")
    @Transactional
    public boolean restoreShipper(Long shipperId) {
        return shipperRepository.restoreById(shipperId) > 0;
    }
    
    @Audited(action = "PERMANENT_DELETE_SHIPPER", entityType = "Shipper", description = "Xóa vĩnh viễn shipper")
    @Transactional
    public boolean permanentlyDeleteShipper(Long shipperId) {
        return shipperRepository.permanentlyDeleteById(shipperId) > 0;
    }
}
