package org.yiqixue.secomm.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新产品评价请求对象（管理员用）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewUpdateRequest {

    @NotBlank(message = "评价状态不能为空")
    @Pattern(regexp = "PENDING|APPROVED|REJECTED", message = "评价状态必须为PENDING、APPROVED或REJECTED")
    private String status;

    @Size(max = 500, message = "审核备注不能超过500个字符")
    private String adminNotes;
}