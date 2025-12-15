package org.yiqixue.secomm.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建产品评价请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewCreateRequest {

    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1星")
    @Max(value = 5, message = "评分最高为5星")
    private Integer rating;

    @Size(max = 100, message = "评价标题不能超过100个字符")
    private String title;

    @NotBlank(message = "评价内容不能为空")
    @Size(min = 10, max = 1000, message = "评价内容长度应为10到1000个字符")
    private String content;

    // 可选：关联的订单ID（用于验证购买）
    private Long orderId;
}