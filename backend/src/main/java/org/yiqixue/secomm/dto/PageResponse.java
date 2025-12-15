package org.yiqixue.secomm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 分页响应包装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    // 数据列表
    private List<T> content;

    // 当前页码（从0开始）
    private int pageNumber;

    // 每页大小
    private int pageSize;

    // 总元素数
    private long totalElements;

    // 总页数
    private int totalPages;

    // 是否是第一页
    private boolean first;

    // 是否是最后一页
    private boolean last;

    // 是否为空
    private boolean empty;
}