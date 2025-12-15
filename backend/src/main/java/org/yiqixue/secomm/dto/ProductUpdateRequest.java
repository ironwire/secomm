package org.yiqixue.secomm.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    
    @NotBlank(message = "SKU不能为空")
    @Size(min = 2, max = 255, message = "SKU长度应为2到255个字符")
    private String sku;
    
    @NotBlank(message = "商品名称不能为空")
    @Size(min = 2, max = 255, message = "商品名称长度应为2到255个字符")
    private String name;
    
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;
    
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.0", message = "单价必须大于等于0")
    private BigDecimal unitPrice;
    
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量必须大于等于0")
    private Integer unitsInStock;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    private Boolean active = true;
}