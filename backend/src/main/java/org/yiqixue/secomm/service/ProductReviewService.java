package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.dto.*;
import org.yiqixue.secomm.entity.Customer;
import org.yiqixue.secomm.entity.Product;
import org.yiqixue.secomm.entity.ProductReview;
import org.yiqixue.secomm.exception.BusinessException;
import org.yiqixue.secomm.exception.ResourceNotFoundException;
import org.yiqixue.secomm.mapper.ProductReviewMapper;
import org.yiqixue.secomm.repository.CustomerRepository;
import org.yiqixue.secomm.repository.ProductRepository;
import org.yiqixue.secomm.repository.ProductReviewRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品评价服务层
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ProductReviewMapper reviewMapper;

    /**
     * 创建产品评价
     */
    @Transactional
    public ProductReviewDTO createReview(Long customerId, ProductReviewCreateRequest request) {
        log.info("创建产品评价 - 客户ID: {}, 产品ID: {}, 评分: {}", 
                customerId, request.getProductId(), request.getRating());

        // 验证产品是否存在
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        // 验证客户是否存在
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        // 检查客户是否已经评价过该产品
        if (reviewRepository.existsByProductIdAndCustomerId(request.getProductId(), customerId)) {
            throw new BusinessException("您已经对该产品进行过评价");
        }

        // 检查是否为认证购买（如果提供了订单ID）
        Boolean verifiedPurchase = false;
        if (request.getOrderId() != null) {
            // TODO: 验证订单是否存在且属于该客户
            verifiedPurchase = true;
        }

        // 创建评价实体
        ProductReview review = ProductReview.builder()
                .productId(request.getProductId())
                .customerId(customerId)
                .orderId(request.getOrderId())
                .rating(request.getRating())
                .title(request.getTitle())
                .content(request.getContent())
                .helpfulCount(0)
                .verifiedPurchase(verifiedPurchase)
                .status("PENDING")
                .build();

        review = reviewRepository.save(review);
        
        log.info("产品评价创建成功 - 评价ID: {}", review.getId());
        
        return reviewMapper.toDTO(review);
    }

    /**
     * 获取产品的所有评价（分页）
     */
    public PageResponse<ProductReviewDTO> getProductReviews(Long productId, int page, int size, String sortBy, String sortDir) {
        log.info("获取产品评价 - 产品ID: {}, 页码: {}, 大小: {}", productId, page, size);

        // 验证产品是否存在
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // 创建排序和分页对象
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 只查询已审核通过的评价
        Page<ProductReview> reviewPage = reviewRepository.findByProductIdAndStatus(productId, "APPROVED", pageable);

        return reviewMapper.toPageResponse(reviewPage);
    }

    /**
     * 获取产品评价汇总信息
     */
    public ProductReviewSummaryDTO getProductReviewSummary(Long productId) {
        log.info("获取产品评价汇总 - 产品ID: {}", productId);

        // 验证产品是否存在
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // 获取平均评分
        BigDecimal averageRating = reviewRepository.getAverageRatingByProductId(productId);
        if (averageRating == null) {
            averageRating = BigDecimal.ZERO;
        }

        // 获取评价总数
        Long totalReviews = reviewRepository.getTotalReviewsByProductId(productId);

        // 获取各星级分布
        List<Object[]> ratingDistributionData = reviewRepository.getRatingDistributionByProductId(productId);
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingDistribution.put(i, 0L);
        }
        for (Object[] data : ratingDistributionData) {
            Integer rating = (Integer) data[0];
            Long count = (Long) data[1];
            ratingDistribution.put(rating, count);
        }

        // 获取最新的几条评价
        Pageable pageable = PageRequest.of(0, 5);
        List<ProductReview> recentReviewsEntity = reviewRepository.getRecentReviewsByProductId(productId, pageable);
        List<ProductReviewDTO> recentReviews = reviewMapper.toDTOList(recentReviewsEntity);

        return ProductReviewSummaryDTO.builder()
                .productId(productId)
                .totalReviews(totalReviews)
                .averageRating(averageRating)
                .ratingDistribution(ratingDistribution)
                .recentReviews(recentReviews)
                .build();
    }

    /**
     * 获取客户的评价（分页）
     */
    public PageResponse<ProductReviewDTO> getCustomerReviews(Long customerId, int page, int size) {
        log.info("获取客户评价 - 客户ID: {}, 页码: {}, 大小: {}", customerId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());
        Page<ProductReview> reviewPage = reviewRepository.findByCustomerId(customerId, pageable);

        return reviewMapper.toPageResponse(reviewPage);
    }

    /**
     * 管理员审核评价
     */
    @Transactional
    public ProductReviewDTO updateReviewStatus(Long reviewId, ProductReviewUpdateRequest request) {
        log.info("更新评价状态 - 评价ID: {}, 新状态: {}", reviewId, request.getStatus());

        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductReview", "id", reviewId));

        review.setStatus(request.getStatus());
        review = reviewRepository.save(review);

        log.info("评价状态更新成功 - 评价ID: {}, 状态: {}", reviewId, request.getStatus());

        return reviewMapper.toDTO(review);
    }

    /**
     * 删除评价
     */
    @Transactional
    public void deleteReview(Long reviewId, Long customerId) {
        log.info("删除评价 - 评价ID: {}, 客户ID: {}", reviewId, customerId);

        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductReview", "id", reviewId));

        // 验证评价是否属于该客户
        if (!review.getCustomerId().equals(customerId)) {
            throw new BusinessException("无权限删除此评价");
        }

        reviewRepository.delete(review);
        
        log.info("评价删除成功 - 评价ID: {}", reviewId);
    }

    /**
     * 管理员获取所有评价（分页，支持状态过滤和搜索）
     */
    public PageResponse<ProductReviewDTO> getAllReviews(int page, int size, String sortBy, String sortDir, String status, String query) {
        log.info("管理员获取所有评价 - 页码: {}, 大小: {}, 状态: {}, 搜索: {}", page, size, status, query);

        // 创建排序和分页对象
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductReview> reviewPage;

        // 根据条件查询
        if (query != null && !query.trim().isEmpty()) {
            // 有搜索关键词
            if (status != null && !status.trim().isEmpty()) {
                reviewPage = reviewRepository.searchReviewsByStatusAndQuery(status, query, pageable);
            } else {
                reviewPage = reviewRepository.searchReviewsByQuery(query, pageable);
            }
        } else if (status != null && !status.trim().isEmpty()) {
            // 只有状态过滤
            reviewPage = reviewRepository.findByStatus(status, pageable);
        } else {
            // 获取所有评价
            reviewPage = reviewRepository.findAll(pageable);
        }

        return reviewMapper.toPageResponse(reviewPage);
    }

    /**
     * 根据状态获取评价（分页）
     */
    public PageResponse<ProductReviewDTO> getReviewsByStatus(String status, int page, int size, String sortBy, String sortDir) {
        log.info("根据状态获取评价 - 状态: {}, 页码: {}, 大小: {}", status, page, size);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductReview> reviewPage = reviewRepository.findByStatus(status, pageable);

        return reviewMapper.toPageResponse(reviewPage);
    }

    /**
     * 搜索评价（分页）
     */
    public PageResponse<ProductReviewDTO> searchReviews(String query, int page, int size, String sortBy, String sortDir) {
        log.info("搜索评价 - 关键词: {}, 页码: {}, 大小: {}", query, page, size);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductReview> reviewPage = reviewRepository.searchReviewsByQuery(query, pageable);

        return reviewMapper.toPageResponse(reviewPage);
    }

    /**
     * 获取评价统计信息
     */
    public Map<String, Object> getReviewStats() {
        log.info("获取评价统计信息");

        Map<String, Object> stats = new HashMap<>();

        // 总评价数
        Long totalReviews = reviewRepository.count();
        stats.put("totalReviews", totalReviews);

        // 各状态评价数
        Long pendingCount = reviewRepository.countByStatus("PENDING");
        Long approvedCount = reviewRepository.countByStatus("APPROVED");
        Long rejectedCount = reviewRepository.countByStatus("REJECTED");

        stats.put("pendingCount", pendingCount);
        stats.put("approvedCount", approvedCount);
        stats.put("rejectedCount", rejectedCount);

        // 今日新增评价数
        Long todayCount = reviewRepository.countTodayReviews();
        stats.put("todayCount", todayCount);

        // 平均评分
        BigDecimal averageRating = reviewRepository.getOverallAverageRating();
        stats.put("averageRating", averageRating != null ? averageRating : BigDecimal.ZERO);

        return stats;
    }

    /**
     * 批量操作评价
     */
    @Transactional
    public void batchUpdateReviews(BatchReviewRequest request) {
        log.info("批量操作评价 - 操作: {}, 数量: {}", request.getAction(), request.getReviewIds().size());

        List<ProductReview> reviews = reviewRepository.findAllById(request.getReviewIds());

        if (reviews.size() != request.getReviewIds().size()) {
            throw new BusinessException("部分评价不存在");
        }

        switch (request.getAction()) {
            case "APPROVE":
                reviews.forEach(review -> review.setStatus("APPROVED"));
                reviewRepository.saveAll(reviews);
                log.info("批量通过评价完成 - 数量: {}", reviews.size());
                break;

            case "REJECT":
                reviews.forEach(review -> review.setStatus("REJECTED"));
                reviewRepository.saveAll(reviews);
                log.info("批量拒绝评价完成 - 数量: {}", reviews.size());
                break;

            case "DELETE":
                reviewRepository.deleteAll(reviews);
                log.info("批量删除评价完成 - 数量: {}", reviews.size());
                break;

            default:
                throw new BusinessException("不支持的操作类型: " + request.getAction());
        }
    }
}
