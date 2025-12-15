package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 产品评价汇总数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewSummaryDTO {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 评价总数
     */
    private Long totalReviews;

    /**
     * 平均评分
     */
    private BigDecimal averageRating;

    /**
     * 各星级评价数量分布
     * Key: 星级(1-5), Value: 该星级的评价数量
     */
    private Map<Integer, Long> ratingDistribution;

    /**
     * 最新的几条评价
     */
    private List<ProductReviewDTO> recentReviews;
}