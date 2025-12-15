package org.yiqixue.secomm.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutRequest {
    
    @NotNull(message = "总金额不能为空")
    @DecimalMin(value = "0.01", message = "总金额必须大于0")
    private BigDecimal totalAmount;
    
    private List<CheckoutItem> items;
    
    @Data
    public static class CheckoutItem {
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        
        @NotNull(message = "数量不能为空")
        private Integer quantity;
        
        @NotNull(message = "单价不能为空")
        private BigDecimal unitPrice;
    }
}