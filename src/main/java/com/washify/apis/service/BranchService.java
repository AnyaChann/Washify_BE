package com.washify.apis.service;

import com.washify.apis.dto.request.BranchRequest;
import com.washify.apis.dto.response.BranchResponse;
import com.washify.apis.entity.Branch;
import com.washify.apis.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Branch
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BranchService {
    
    private final BranchRepository branchRepository;
    
    /**
     * Tạo chi nhánh mới
     */
    public BranchResponse createBranch(BranchRequest request) {
        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        branch.setManagerName(request.getManagerName());
        
        Branch savedBranch = branchRepository.save(branch);
        return mapToBranchResponse(savedBranch);
    }
    
    /**
     * Lấy thông tin chi nhánh theo ID
     */
    @Transactional(readOnly = true)
    public BranchResponse getBranchById(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh với ID: " + branchId));
        return mapToBranchResponse(branch);
    }
    
    /**
     * Lấy tất cả chi nhánh
     */
    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::mapToBranchResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật chi nhánh
     */
    public BranchResponse updateBranch(Long branchId, BranchRequest request) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh với ID: " + branchId));
        
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        branch.setManagerName(request.getManagerName());
        
        Branch updatedBranch = branchRepository.save(branch);
        return mapToBranchResponse(updatedBranch);
    }
    
    /**
     * Xóa chi nhánh
     */
    public void deleteBranch(Long branchId) {
        if (!branchRepository.existsById(branchId)) {
            throw new RuntimeException("Không tìm thấy chi nhánh với ID: " + branchId);
        }
        branchRepository.deleteById(branchId);
    }
    
    /**
     * Map Entity sang DTO Response
     */
    private BranchResponse mapToBranchResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .managerName(branch.getManagerName())
                .isActive(branch.getIsActive())
                .createdAt(branch.getCreatedAt())
                .deletedAt(branch.getDeletedAt())
                .build();
    }
    
    // ========================================
    // ENHANCEMENTS - Phase 3: Statistics & Analytics
    // ========================================
    
    /**
     * Lấy thống kê tất cả chi nhánh (so sánh hiệu suất giữa các chi nhánh)
     */
    @Transactional(readOnly = true)
    public List<BranchStatistics> getAllBranchStatistics() {
        List<Branch> branches = branchRepository.findAll();
        
        return branches.stream()
                .map(branch -> {
                    Long totalOrders = branchRepository.countOrdersByBranchId(branch.getId());
                    Long completedOrders = branchRepository.countCompletedOrdersByBranchId(branch.getId());
                    Double totalRevenue = branchRepository.sumRevenueByBranchId(branch.getId());
                    
                    // Tính tỷ lệ hoàn thành
                    double completionRate = 0.0;
                    if (totalOrders > 0) {
                        completionRate = (completedOrders * 100.0) / totalOrders;
                    }
                    
                    return new BranchStatistics(
                            branch.getId(),
                            branch.getName(),
                            totalOrders,
                            completedOrders,
                            totalRevenue != null ? totalRevenue : 0.0,
                            completionRate
                    );
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy thống kê chi tiết của một chi nhánh
     */
    @Transactional(readOnly = true)
    public BranchDetailStatistics getBranchDetailStatistics(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh với ID: " + branchId));
        
        Long totalOrders = branchRepository.countOrdersByBranchId(branchId);
        Long completedOrders = branchRepository.countCompletedOrdersByBranchId(branchId);
        Double totalRevenue = branchRepository.sumRevenueByBranchId(branchId);
        Double averageOrderValue = branchRepository.getAverageOrderValueByBranchId(branchId);
        
        // Tính tỷ lệ hoàn thành
        double completionRate = 0.0;
        if (totalOrders > 0) {
            completionRate = (completedOrders * 100.0) / totalOrders;
        }
        
        return new BranchDetailStatistics(
                branch.getId(),
                branch.getName(),
                branch.getAddress(),
                branch.getManagerName(),
                totalOrders,
                completedOrders,
                totalRevenue != null ? totalRevenue : 0.0,
                averageOrderValue != null ? averageOrderValue : 0.0,
                completionRate,
                branch.getIsActive()
        );
    }
    
    /**
     * Inner class chứa thống kê tổng quan của chi nhánh (để so sánh)
     */
    public static class BranchStatistics {
        public final Long branchId;
        public final String branchName;
        public final Long totalOrders;
        public final Long completedOrders;
        public final Double totalRevenue;
        public final Double completionRate; // Tỷ lệ hoàn thành (%)
        
        public BranchStatistics(Long branchId, String branchName, Long totalOrders,
                                Long completedOrders, Double totalRevenue, Double completionRate) {
            this.branchId = branchId;
            this.branchName = branchName;
            this.totalOrders = totalOrders;
            this.completedOrders = completedOrders;
            this.totalRevenue = totalRevenue;
            this.completionRate = completionRate;
        }
    }
    
    /**
     * Inner class chứa thống kê chi tiết của một chi nhánh
     */
    public static class BranchDetailStatistics {
        public final Long branchId;
        public final String branchName;
        public final String address;
        public final String managerName;
        public final Long totalOrders;
        public final Long completedOrders;
        public final Double totalRevenue;
        public final Double averageOrderValue;
        public final Double completionRate; // Tỷ lệ hoàn thành (%)
        public final Boolean isActive;
        
        public BranchDetailStatistics(Long branchId, String branchName, String address,
                                      String managerName, Long totalOrders, Long completedOrders,
                                      Double totalRevenue, Double averageOrderValue,
                                      Double completionRate, Boolean isActive) {
            this.branchId = branchId;
            this.branchName = branchName;
            this.address = address;
            this.managerName = managerName;
            this.totalOrders = totalOrders;
            this.completedOrders = completedOrders;
            this.totalRevenue = totalRevenue;
            this.averageOrderValue = averageOrderValue;
            this.completionRate = completionRate;
            this.isActive = isActive;
        }
    }
    
    // ========================================
    // OPERATIONAL ENHANCEMENTS - Phase 3
    // ========================================
    
    /**
     * Tìm kiếm chi nhánh theo nhiều tiêu chí
     */
    @Transactional(readOnly = true)
    public List<BranchResponse> searchBranches(String name, String address, Boolean isActive) {
        List<Branch> branches = branchRepository.findAll();
        
        return branches.stream()
                .filter(b -> name == null || b.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(b -> address == null || b.getAddress().toLowerCase().contains(address.toLowerCase()))
                .filter(b -> isActive == null || b.getIsActive().equals(isActive))
                .map(this::mapToBranchResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Tìm chi nhánh gần vị trí (tính theo khoảng cách Haversine)
     * Simple implementation - trong production nên dùng PostGIS hoặc MongoDB geospatial queries
     */
    @Transactional(readOnly = true)
    public List<BranchResponse> findNearbyBranches(Double userLat, Double userLng, Double radiusKm) {
        List<Branch> allBranches = branchRepository.findAll();
        
        return allBranches.stream()
                .filter(Branch::getIsActive) // Chỉ lấy chi nhánh đang hoạt động
                .filter(b -> {
                    // Giả sử latitude/longitude được lưu trong địa chỉ hoặc có trường riêng
                    // Ở đây đơn giản hóa: trả về tất cả active branches
                    // TODO: Implement proper geospatial calculation when lat/lng fields are added
                    return true;
                })
                .map(this::mapToBranchResponse)
                .collect(Collectors.toList());
    }
}

