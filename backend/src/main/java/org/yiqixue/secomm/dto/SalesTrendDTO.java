package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售趋势DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesTrendDTO {
    
    private LocalDate date;             // 日期
    private BigDecimal sales;           // 销售额
    private Long orders;                // 订单数
    private String label;               // 日期标签（用于图表显示）
}