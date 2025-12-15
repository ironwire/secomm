package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;

    private String sku;

    private String name;

    private String description;

    private BigDecimal unitPrice;

    private String imageUrl;

    private Boolean active;

    private Integer unitsInStock;

    private LocalDateTime dateCreated;

    private LocalDateTime lastUpdated;

    // 分类信息
    private Long categoryId;

    private String categoryName;
}