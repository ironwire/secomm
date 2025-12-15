package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;

    private Long customerId;

    private String orderNumber;

    private String status;

    private BigDecimal totalAmount;

    private LocalDateTime orderDate;

    private List<OrderItemDTO> orderItems;

    // 客户信息
    private String customerName;
    private String customerEmail;
}