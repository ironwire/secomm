package org.yiqixue.secomm.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.yiqixue.secomm.dto.PageResponse;
import org.yiqixue.secomm.dto.ProductReviewDTO;
import org.yiqixue.secomm.entity.ProductReview;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductReview Entity 和 DTO 之间的转换工具类
 */
@Component
public class ProductReviewMapper {

    /**
     * ProductReview Entity 转 DTO
     */
    public ProductReviewDTO toDTO(ProductReview review) {
        if (review == null) {
            return null;
        }

        return ProductReviewDTO.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .customerId(review.getCustomerId())
                .orderId(review.getOrderId())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .helpfulCount(review.getHelpfulCount())
                .verifiedPurchase(review.getVerifiedPurchase())
                .status(review.getStatus())
                .dateCreated(review.getDateCreated())
                .lastUpdated(review.getLastUpdated())
                .customerName(review.getCustomer() != null ? review.getCustomer().getFirstName() + " " + review.getCustomer().getLastName() : null)
                .productName(review.getProduct() != null ? review.getProduct().getName() : null)
                .build();
    }

    /**
     * ProductReview Entity 列表转 DTO 列表
     */
    public List<ProductReviewDTO> toDTOList(List<ProductReview> reviews) {
        return reviews.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Page<ProductReview> 转 PageResponse<ProductReviewDTO>
     */
    public PageResponse<ProductReviewDTO> toPageResponse(Page<ProductReview> reviewPage) {
        List<ProductReviewDTO> reviewDTOs = toDTOList(reviewPage.getContent());

        return PageResponse.<ProductReviewDTO>builder()
                .content(reviewDTOs)
                .pageNumber(reviewPage.getNumber())
                .pageSize(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .first(reviewPage.isFirst())
                .last(reviewPage.isLast())
                .empty(reviewPage.isEmpty())
                .build();
    }
}