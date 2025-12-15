package org.yiqixue.secomm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.*;
import org.yiqixue.secomm.security.UserPrincipal;
import org.yiqixue.secomm.service.ProductReviewService;

/**
 * 产品评价控制器
 * 提供产品评价相关的REST API接口
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "产品评价管理", description = "产品评价相关的API接口")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductReviewController {

    private final ProductReviewService reviewService;

    /**
     * 获取产品评价列表（分页）
     * GET /api/products/{productId}/reviews
     */
    @GetMapping("/{productId}/reviews")
    @Operation(summary = "获取产品评价", description = "分页获取指定产品的评价列表")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewDTO>>> getProductReviews(
            @Parameter(description = "产品ID")
            @PathVariable Long productId,
            
            @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "排序字段")
            @RequestParam(defaultValue = "dateCreated") String sortBy,
            
            @Parameter(description = "排序方向：asc或desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("API调用 - 获取产品评价: productId={}, page={}, size={}", productId, page, size);

        PageResponse<ProductReviewDTO> reviews = reviewService.getProductReviews(productId, page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    /**
     * 获取产品评价汇总信息
     * GET /api/products/{productId}/reviews/summary
     */
    @GetMapping("/{productId}/reviews/summary")
    @Operation(summary = "获取产品评价汇总", description = "获取产品的评价统计信息")
    public ResponseEntity<ApiResponse<ProductReviewSummaryDTO>> getProductReviewSummary(
            @Parameter(description = "产品ID")
            @PathVariable Long productId) {

        log.info("API调用 - 获取产品评价汇总: productId={}", productId);

        ProductReviewSummaryDTO summary = reviewService.getProductReviewSummary(productId);

        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * 创建产品评价
     * POST /api/products/{productId}/reviews
     */
    @PostMapping("/{productId}/reviews")
    @Operation(summary = "创建产品评价", description = "为指定产品创建评价")
    public ResponseEntity<ApiResponse<ProductReviewDTO>> createReview(
            @Parameter(description = "产品ID")
            @PathVariable Long productId,
            
            @Valid @RequestBody ProductReviewCreateRequest request,
            
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("API调用 - 创建产品评价: productId={}, customerId={}, rating={}", 
                productId, userPrincipal.getId(), request.getRating());

        // 确保请求中的产品ID与路径参数一致
        request.setProductId(productId);

        try {
            ProductReviewDTO review = reviewService.createReview(userPrincipal.getId(), request);
            
            return ResponseEntity.ok(ApiResponse.success("评价提交成功，等待审核", review));
            
        } catch (Exception e) {
            log.error("创建产品评价失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取当前客户的评价列表
     * GET /api/reviews/my
     */
    @GetMapping("/reviews/my")
    @Operation(summary = "获取我的评价", description = "获取当前客户的评价列表")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewDTO>>> getMyReviews(
            @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size,
            
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("API调用 - 获取我的评价: customerId={}, page={}, size={}", 
                userPrincipal.getId(), page, size);

        PageResponse<ProductReviewDTO> reviews = reviewService.getCustomerReviews(userPrincipal.getId(), page, size);

        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    /**
     * 删除评价
     * DELETE /api/reviews/{reviewId}
     */
    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "删除评价", description = "删除指定的评价")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @Parameter(description = "评价ID")
            @PathVariable Long reviewId,
            
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("API调用 - 删除评价: reviewId={}, customerId={}", reviewId, userPrincipal.getId());

        try {
            reviewService.deleteReview(reviewId, userPrincipal.getId());
            
            return ResponseEntity.ok(ApiResponse.success(null, "评价删除成功"));
            
        } catch (Exception e) {
            log.error("删除评价失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 管理员审核评价
     * PUT /api/admin/reviews/{reviewId}/status
     */
    @PutMapping("/admin/reviews/{reviewId}/status")
    @Operation(summary = "审核评价", description = "管理员审核评价状态")
    public ResponseEntity<ApiResponse<ProductReviewDTO>> updateReviewStatus(
            @Parameter(description = "评价ID")
            @PathVariable Long reviewId,
            
            @Valid @RequestBody ProductReviewUpdateRequest request) {

        log.info("API调用 - 审核评价: reviewId={}, status={}", reviewId, request.getStatus());

        try {
            ProductReviewDTO review = reviewService.updateReviewStatus(reviewId, request);
            
            return ResponseEntity.ok(ApiResponse.success("评价状态更新成功", review));
            
        } catch (Exception e) {
            log.error("审核评价失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 管理员获取所有评价列表（分页）
     * GET /api/admin/reviews
     */
    @GetMapping("/admin/reviews")
    @Operation(summary = "获取所有评价", description = "管理员分页获取所有评价列表")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewDTO>>> getAllReviews(
            @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "排序字段")
            @RequestParam(defaultValue = "dateCreated") String sortBy,
            
            @Parameter(description = "排序方向：asc或desc")
            @RequestParam(defaultValue = "desc") String sortDir,
            
            @Parameter(description = "状态过滤")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "搜索关键词")
            @RequestParam(required = false) String query) {

        log.info("API调用 - 管理员获取所有评价: page={}, size={}, status={}, query={}", 
                page, size, status, query);

        PageResponse<ProductReviewDTO> reviews = reviewService.getAllReviews(page, size, sortBy, sortDir, status, query);

        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    /**
     * 管理员根据状态获取评价列表（分页）
     * GET /api/admin/reviews/status/{status}
     */
    @GetMapping("/admin/reviews/status/{status}")
    @Operation(summary = "按状态获取评价", description = "管理员根据状态分页获取评价列表")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewDTO>>> getReviewsByStatus(
            @Parameter(description = "评价状态")
            @PathVariable String status,
            
            @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "排序字段")
            @RequestParam(defaultValue = "dateCreated") String sortBy,
            
            @Parameter(description = "排序方向：asc或desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("API调用 - 按状态获取评价: status={}, page={}, size={}", status, page, size);

        PageResponse<ProductReviewDTO> reviews = reviewService.getReviewsByStatus(status, page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    /**
     * 管理员搜索评价
     * GET /api/admin/reviews/search
     */
    @GetMapping("/admin/reviews/search")
    @Operation(summary = "搜索评价", description = "管理员根据关键词搜索评价")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewDTO>>> searchReviews(
            @Parameter(description = "搜索关键词")
            @RequestParam String query,
            
            @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "排序字段")
            @RequestParam(defaultValue = "dateCreated") String sortBy,
            
            @Parameter(description = "排序方向：asc或desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("API调用 - 搜索评价: query={}, page={}, size={}", query, page, size);

        PageResponse<ProductReviewDTO> reviews = reviewService.searchReviews(query, page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    /**
     * 管理员获取评价统计信息
     * GET /api/admin/reviews/stats
     */
    @GetMapping("/admin/reviews/stats")
    @Operation(summary = "获取评价统计", description = "管理员获取评价统计信息")
    public ResponseEntity<ApiResponse<Object>> getReviewStats() {
        log.info("API调用 - 获取评价统计");

        Object stats = reviewService.getReviewStats();

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * 管理员批量操作评价
     * POST /api/admin/reviews/batch
     */
    @PostMapping("/admin/reviews/batch")
    @Operation(summary = "批量操作评价", description = "管理员批量审核或删除评价")
    public ResponseEntity<ApiResponse<String>> batchUpdateReviews(
            @Valid @RequestBody BatchReviewRequest request) {

        log.info("API调用 - 批量操作评价: action={}, count={}", 
                request.getAction(), request.getReviewIds().size());

        try {
            reviewService.batchUpdateReviews(request);
            
            return ResponseEntity.ok(ApiResponse.success(null, "批量操作完成"));
            
        } catch (Exception e) {
            log.error("批量操作评价失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
