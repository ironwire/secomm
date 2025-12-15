package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.yiqixue.secomm.dto.*;
import org.yiqixue.secomm.entity.Order;
import org.yiqixue.secomm.repository.OrderRepository;
import org.yiqixue.secomm.repository.UserRepository;
import org.yiqixue.secomm.repository.ProductReviewRepository;
import org.yiqixue.secomm.repository.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    /**
     * 获取关键指标
     */
    public DashboardMetricsDTO getMetrics(String period) {
        log.info("获取关键指标 - 周期: {}", period);

        LocalDateTime[] dateRange = getDateRange(period);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];

        // 获取当前周期数据
        BigDecimal currentSales = orderRepository.getTotalSalesByDateRange(startDate, endDate);
        Long currentOrders = orderRepository.countOrdersByDateRange(startDate, endDate);
        Long currentUsers = userRepository.countActiveUsersByDateRange(startDate, endDate);
        BigDecimal currentRating = reviewRepository.getAverageRatingByDateRange(startDate, endDate);

        // 获取上一周期数据用于计算趋势
        LocalDateTime[] prevDateRange = getPreviousDateRange(period);
        BigDecimal prevSales = orderRepository.getTotalSalesByDateRange(prevDateRange[0], prevDateRange[1]);
        Long prevOrders = orderRepository.countOrdersByDateRange(prevDateRange[0], prevDateRange[1]);
        Long prevUsers = userRepository.countActiveUsersByDateRange(prevDateRange[0], prevDateRange[1]);
        BigDecimal prevRating = reviewRepository.getAverageRatingByDateRange(prevDateRange[0], prevDateRange[1]);

        return DashboardMetricsDTO.builder()
                .totalSales(currentSales != null ? currentSales : BigDecimal.ZERO)
                .salesTrend(calculateTrend(currentSales, prevSales))
                .totalOrders(currentOrders != null ? currentOrders : 0L)
                .ordersTrend(calculateTrend(currentOrders, prevOrders))
                .activeUsers(currentUsers != null ? currentUsers : 0L)
                .usersTrend(calculateTrend(currentUsers, prevUsers))
                .averageRating(currentRating != null ? currentRating : BigDecimal.ZERO)
                .ratingTrend(calculateTrend(currentRating, prevRating))
                .period(period)
                .build();
    }

    /**
     * 获取订单状态分布
     */
    public List<OrderStatusDistributionDTO> getOrderStatusDistribution(String period) {
        log.info("获取订单状态分布 - 周期: {}", period);

        LocalDateTime[] dateRange = getDateRange(period);
        List<Object[]> statusCounts = orderRepository.getOrderStatusDistribution(dateRange[0], dateRange[1]);
        
        Long totalOrders = statusCounts.stream()
                .mapToLong(row -> (Long) row[1])
                .sum();

        return statusCounts.stream()
                .map(row -> {
                    Order.OrderStatus status = (Order.OrderStatus) row[0];
                    Long count = (Long) row[1];
                    Double percentage = totalOrders > 0 ? (count.doubleValue() / totalOrders * 100) : 0.0;
                    
                    return OrderStatusDistributionDTO.builder()
                            .status(status.name())
                            .statusText(getStatusText(status))
                            .count(count)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取最近订单
     */
    public List<RecentOrderDTO> getRecentOrders(int limit) {
        log.info("获取最近订单 - 数量: {}", limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<Order> orders = orderRepository.findRecentOrdersWithCustomer(pageable);

        return orders.stream()
                .map(order -> RecentOrderDTO.builder()
                        .id(order.getId())
                        .orderNumber(order.getOrderNumber())
                        .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
                        .customerEmail(order.getCustomer().getEmail())
                        .amount(order.getTotalAmount())
                        .status(order.getStatus().name())
                        .statusText(getStatusText(order.getStatus()))
                        .orderDate(order.getDateCreated())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取畅销产品
     */
    public List<TopProductDTO> getTopProducts(String period, int limit) {
        log.info("获取畅销产品 - 周期: {}, 数量: {}", period, limit);

        LocalDateTime[] dateRange = getDateRange(period);
        List<Object[]> topProducts = orderRepository.getTopProductsByDateRange(
                dateRange[0], dateRange[1], limit);

        // 计算总销售额用于百分比计算
        BigDecimal totalRevenue = topProducts.stream()
                .map(row -> {
                    Object revenueObj = row[2];
                    return revenueObj instanceof Long ? 
                        BigDecimal.valueOf((Long) revenueObj) : 
                        (BigDecimal) revenueObj;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return topProducts.stream()
                .map(row -> {
                    Long productId = (Long) row[0];
                    // SUM(quantity) 返回 BigDecimal，需要转换为 Long
                    Object salesObj = row[1];
                    Long salesCount = salesObj instanceof BigDecimal ? 
                        ((BigDecimal) salesObj).longValue() : 
                        (Long) salesObj;
                    
                    // 收入已经是 BigDecimal
                    BigDecimal revenue = (BigDecimal) row[2];
                    
                    // 计算百分比
                    Double percentage = totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
                        revenue.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                               .multiply(BigDecimal.valueOf(100))
                               .doubleValue() : 0.0;
                    
                    return TopProductDTO.builder()
                            .id(productId)
                            .name("Product " + productId)
                            .category("Unknown")
                            .sales(salesCount)
                            .revenue(revenue)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取快速统计
     */
    public QuickStatsDTO getQuickStats() {
        log.info("获取快速统计");

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Long todayNewUsers = userRepository.countUsersByDateRange(startOfDay, endOfDay);
        Long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        Long customerFeedbacks = reviewRepository.countByStatus("PENDING");
        Long lowStockProducts = productRepository.countLowStockProducts(10); // 库存少于10的产品

        return QuickStatsDTO.builder()
                .todayNewUsers(todayNewUsers)
                .pendingOrders(pendingOrders)
                .customerFeedbacks(customerFeedbacks)
                .lowStockProducts(lowStockProducts)
                .build();
    }

    /**
     * 获取销售趋势
     */
    public List<SalesTrendDTO> getSalesTrend(String period) {
        log.info("获取销售趋势 - 周期: {}", period);

        LocalDateTime[] dateRange = getDateRange(period);
        List<Object[]> trendData = orderRepository.getSalesTrendByDateRange(dateRange[0], dateRange[1]);

        return trendData.stream()
                .map(row -> {
                    LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
                    BigDecimal sales = (BigDecimal) row[1];
                    Long orders = (Long) row[2];

                    return SalesTrendDTO.builder()
                            .date(date)
                            .sales(sales != null ? sales : BigDecimal.ZERO)
                            .orders(orders != null ? orders : 0L)
                            .label(formatDateLabel(date, period))
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 辅助方法
    private LocalDateTime[] getDateRange(String period) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start;

        switch (period) {
            case "Today":
                start = end.toLocalDate().atStartOfDay();
                break;
            case "7Days":
                start = end.minusDays(7);
                break;
            case "30Days":
                start = end.minusDays(30);
                break;
            case "90Days":
                start = end.minusDays(90);
                break;
            default:
                start = end.minusDays(7);
        }

        return new LocalDateTime[]{start, end};
    }

    private LocalDateTime[] getPreviousDateRange(String period) {
        LocalDateTime[] current = getDateRange(period);
        long days = java.time.Duration.between(current[0], current[1]).toDays();
        
        LocalDateTime end = current[0];
        LocalDateTime start = end.minusDays(days);
        
        return new LocalDateTime[]{start, end};
    }

    private Double calculateTrend(Object current, Object previous) {
        if (current == null || previous == null) return 0.0;
        
        double currentVal = getDoubleValue(current);
        double prevVal = getDoubleValue(previous);
        
        if (prevVal == 0) return currentVal > 0 ? 100.0 : 0.0;
        
        return ((currentVal - prevVal) / prevVal) * 100;
    }

    private double getDoubleValue(Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        return 0.0;
    }

    private String getStatusText(Order.OrderStatus status) {
        Map<Order.OrderStatus, String> statusMap = Map.of(
                Order.OrderStatus.PENDING, "待处理",
                Order.OrderStatus.CONFIRMED, "已确认",
                Order.OrderStatus.SHIPPED, "已发货",
                Order.OrderStatus.DELIVERED, "已送达",
                Order.OrderStatus.CANCELLED, "已取消"
        );
        return statusMap.getOrDefault(status, status.name());
    }

    private String formatDateLabel(LocalDate date, String period) {
        if ("Today".equals(period)) {
            return date.toString();
        } else {
            return date.getMonthValue() + "/" + date.getDayOfMonth();
        }
    }
}