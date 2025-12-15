package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单状态分布DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusDistributionDTO {
    
    private String status;      // 订单状态
    private String statusText;  // 状态中文名称
    private Long count;         // 数量
    private Double percentage;  // 百分比
}