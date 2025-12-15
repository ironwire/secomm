package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 最近订单DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentOrderDTO {
    
    private Long id;                    // 订单ID
    private String orderNumber;         // 订单号
    private String customerName;        // 客户姓名
    private String customerEmail;       // 客户邮箱
    private BigDecimal amount;          // 订单金额
    private String status;              // 订单状态
    private String statusText;          // 状态中文名称
    private LocalDateTime orderDate;    // 下单时间
}