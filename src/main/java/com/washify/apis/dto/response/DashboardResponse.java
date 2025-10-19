package com.washify.apis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO response cho Dashboard/Statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    // Tổng quan
    private Long totalOrders;
    private Long totalCustomers;
    private Long totalRevenue;
    private Long pendingOrders;
    private Long processingOrders;
    private Long completedOrders;

    // Doanh thu
    private BigDecimal todayRevenue;
    private BigDecimal weekRevenue;
    private BigDecimal monthRevenue;
    private BigDecimal yearRevenue;

    // Đơn hàng
    private Long todayOrders;
    private Long weekOrders;
    private Long monthOrders;
    private Long yearOrders;

    // Khách hàng
    private Long newCustomersToday;
    private Long newCustomersWeek;
    private Long newCustomersMonth;

    // Dịch vụ phổ biến
    private String mostPopularService;
    private Long mostPopularServiceCount;

    // Chi nhánh
    private String topBranch;
    private BigDecimal topBranchRevenue;

    // Thời gian cập nhật
    private LocalDateTime lastUpdated;
}
