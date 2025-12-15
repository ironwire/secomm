package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 驾驶舱关键指标DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardMetricsDTO {
    
    private BigDecimal totalSales;        // 总销售额
    private Double salesTrend;            // 销售趋势百分比
    
    private Long totalOrders;             // 总订单数
    private Double ordersTrend;           // 订单趋势百分比
    
    private Long activeUsers;             // 活跃用户数
    private Double usersTrend;            // 用户趋势百分比
    
    private BigDecimal averageRating;     // 平均评分
    private Double ratingTrend;           // 评分趋势百分比
    
    private String period;                // 统计周期
}