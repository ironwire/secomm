package org.yiqixue.secomm.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量操作评价请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchReviewRequest {

    /**
     * 评价ID列表
     */
    @NotEmpty(message = "评价ID列表不能为空")
    private List<Long> reviewIds;

    /**
     * 操作类型：APPROVE（批量通过）、REJECT（批量拒绝）、DELETE（批量删除）
     */
    @NotNull(message = "操作类型不能为空")
    @Pattern(regexp = "APPROVE|REJECT|DELETE", message = "操作类型必须为APPROVE、REJECT或DELETE")
    private String action;

    /**
     * 管理员备注（可选）
     */
    @Size(max = 500, message = "管理员备注不能超过500个字符")
    private String adminNotes;
}