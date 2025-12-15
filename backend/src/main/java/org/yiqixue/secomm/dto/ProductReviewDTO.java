package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 产品评价数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewDTO {

    private Long id;

    private Long productId;

    private Long customerId;

    private Long orderId;

    private Integer rating;

    private String title;

    private String content;

    private Integer helpfulCount;

    private Boolean verifiedPurchase;

    private String status;

    private LocalDateTime dateCreated;

    private LocalDateTime lastUpdated;

    // 扩展字段 - 客户信息
    private String customerName;

    // 扩展字段 - 产品信息
    private String productName;
}