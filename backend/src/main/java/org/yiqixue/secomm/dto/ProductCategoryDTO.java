package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品分类数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryDTO {

    private Long id;

    private String categoryName;

    private String categoryEnglishName;

    // 可选：该分类下的商品数量
    private Long productCount;
}