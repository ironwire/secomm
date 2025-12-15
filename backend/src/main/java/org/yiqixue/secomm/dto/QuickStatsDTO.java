package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 快速统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuickStatsDTO {
    
    private Long todayNewUsers;         // 今日新用户
    private Long pendingOrders;         // 待处理订单
    private Long customerFeedbacks;     // 客户反馈数
    private Long lowStockProducts;      // 低库存产品数
}