package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车商品项数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {

    private Long id;

    private Long cartId;

    private Long productId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    private LocalDateTime dateAdded;

    private LocalDateTime lastUpdated;

    // 商品信息
    private String productName;
    private String productSku;
    private String productImageUrl;
    private String productDescription;
    private Integer unitsInStock;
}