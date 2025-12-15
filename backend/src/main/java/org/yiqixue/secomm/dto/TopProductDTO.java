package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 畅销产品DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopProductDTO {
    
    private Long id;                    // 产品ID
    private String name;                // 产品名称
    private String category;            // 产品分类
    private Long sales;                 // 销售数量
    private BigDecimal revenue;         // 销售收入
    private Double percentage;          // 销售占比百分比
    private String imageUrl;            // 产品图片URL
}