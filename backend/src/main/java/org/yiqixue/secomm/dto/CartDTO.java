package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {

    private Long id;

    private Long customerId;

    private String status;

    private BigDecimal totalPrice;

    private Integer totalQuantity;

    private LocalDateTime dateCreated;

    private LocalDateTime lastUpdated;

    private List<CartItemDTO> cartItems;

    // 客户信息
    private String customerName;
}