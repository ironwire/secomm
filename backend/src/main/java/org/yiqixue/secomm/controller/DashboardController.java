package org.yiqixue.secomm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.ApiResponse;
import org.yiqixue.secomm.dto.DashboardMetricsDTO;
import org.yiqixue.secomm.dto.OrderStatusDistributionDTO;
import org.yiqixue.secomm.dto.RecentOrderDTO;
import org.yiqixue.secomm.dto.TopProductDTO;
import org.yiqixue.secomm.dto.QuickStatsDTO;
import org.yiqixue.secomm.dto.SalesTrendDTO;
import org.yiqixue.secomm.service.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "经理驾驶舱", description = "经理驾驶舱数据统计API")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取关键指标数据
     */
    @GetMapping("/metrics")
    @Operation(summary = "获取关键指标", description = "获取总销售、总订单、活跃用户、平均评分等关键指标")
    public ResponseEntity<ApiResponse<DashboardMetricsDTO>> getMetrics(
            @Parameter(description = "时间周期：Today, 7Days, 30Days, 90Days")
            @RequestParam(defaultValue = "7Days") String period) {
        
        log.info("API调用 - 获取关键指标: period={}", period);
        
        DashboardMetricsDTO metrics = dashboardService.getMetrics(period);
        
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    /**
     * 获取订单状态分布
     */
    @GetMapping("/order-status-distribution")
    @Operation(summary = "获取订单状态分布", description = "获取各状态订单的数量分布")
    public ResponseEntity<ApiResponse<List<OrderStatusDistributionDTO>>> getOrderStatusDistribution(
            @Parameter(description = "时间周期")
            @RequestParam(defaultValue = "7Days") String period) {
        
        log.info("API调用 - 获取订单状态分布: period={}", period);
        
        List<OrderStatusDistributionDTO> distribution = dashboardService.getOrderStatusDistribution(period);
        
        return ResponseEntity.ok(ApiResponse.success(distribution));
    }

    /**
     * 获取最近订单
     */
    @GetMapping("/recent-orders")
    @Operation(summary = "获取最近订单", description = "获取最近的订单列表")
    public ResponseEntity<ApiResponse<List<RecentOrderDTO>>> getRecentOrders(
            @Parameter(description = "返回数量")
            @RequestParam(defaultValue = "5") int limit) {
        
        log.info("API调用 - 获取最近订单: limit={}", limit);
        
        List<RecentOrderDTO> recentOrders = dashboardService.getRecentOrders(limit);
        
        return ResponseEntity.ok(ApiResponse.success(recentOrders));
    }

    /**
     * 获取畅销产品
     */
    @GetMapping("/top-products")
    @Operation(summary = "获取畅销产品", description = "获取销量最高的产品列表")
    public ResponseEntity<ApiResponse<List<TopProductDTO>>> getTopProducts(
            @Parameter(description = "时间周期")
            @RequestParam(defaultValue = "7Days") String period,
            @Parameter(description = "返回数量")
            @RequestParam(defaultValue = "5") int limit) {
        
        log.info("API调用 - 获取畅销产品: period={}, limit={}", period, limit);
        
        List<TopProductDTO> topProducts = dashboardService.getTopProducts(period, limit);
        
        return ResponseEntity.ok(ApiResponse.success(topProducts));
    }

    /**
     * 获取快速统计
     */
    @GetMapping("/quick-stats")
    @Operation(summary = "获取快速统计", description = "获取今日新用户、待处理订单、客户反馈等快速统计")
    public ResponseEntity<ApiResponse<QuickStatsDTO>> getQuickStats() {
        
        log.info("API调用 - 获取快速统计");
        
        QuickStatsDTO quickStats = dashboardService.getQuickStats();
        
        return ResponseEntity.ok(ApiResponse.success(quickStats));
    }

    /**
     * 获取销售趋势数据
     */
    @GetMapping("/sales-trend")
    @Operation(summary = "获取销售趋势", description = "获取指定时间周期的销售趋势数据")
    public ResponseEntity<ApiResponse<List<SalesTrendDTO>>> getSalesTrend(
            @Parameter(description = "时间周期")
            @RequestParam(defaultValue = "7Days") String period) {
        
        log.info("API调用 - 获取销售趋势: period={}", period);
        
        List<SalesTrendDTO> salesTrend = dashboardService.getSalesTrend(period);
        
        return ResponseEntity.ok(ApiResponse.success(salesTrend));
    }
}