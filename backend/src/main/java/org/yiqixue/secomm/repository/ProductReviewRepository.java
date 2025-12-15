package org.yiqixue.secomm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.ProductReview;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    /**
     * 根据产品ID查找评价（分页）
     */
    Page<ProductReview> findByProductId(Long productId, Pageable pageable);

    /**
     * 根据产品ID和状态查找评价（分页）
     */
    Page<ProductReview> findByProductIdAndStatus(Long productId, String status, Pageable pageable);

    /**
     * 根据客户ID查找评价（分页）
     */
    Page<ProductReview> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * 根据评价状态查找评价（分页）
     */
    Page<ProductReview> findByStatus(String status, Pageable pageable);

    /**
     * 检查客户是否已对该产品评价过
     */
    boolean existsByProductIdAndCustomerId(Long productId, Long customerId);

    /**
     * 根据产品ID和客户ID查找评价
     */
    Optional<ProductReview> findByProductIdAndCustomerId(Long productId, Long customerId);

    /**
     * 计算产品的平均评分
     */
    @Query("SELECT COALESCE(1.0 * AVG(pr.rating), 0.0) FROM ProductReview pr WHERE pr.productId = :productId AND pr.status = 'APPROVED'")
    BigDecimal getAverageRatingByProductId(@Param("productId") Long productId);

    /**
     * 统计产品的评价总数
     */
    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE pr.productId = :productId AND pr.status = 'APPROVED'")
    Long getTotalReviewsByProductId(@Param("productId") Long productId);

    /**
     * 获取产品各星级评价数量分布
     */
    @Query("SELECT pr.rating, COUNT(pr) FROM ProductReview pr WHERE pr.productId = :productId AND pr.status = 'APPROVED' GROUP BY pr.rating")
    List<Object[]> getRatingDistributionByProductId(@Param("productId") Long productId);

    /**
     * 获取产品最新的几条评价
     */
    @Query("SELECT pr FROM ProductReview pr WHERE pr.productId = :productId AND pr.status = 'APPROVED' ORDER BY pr.dateCreated DESC")
    List<ProductReview> getRecentReviewsByProductId(@Param("productId") Long productId, Pageable pageable);

    /**
     * 根据订单ID查找评价
     */
    List<ProductReview> findByOrderId(Long orderId);

    /**
     * 检查客户是否对该订单中的产品评价过
     */
    boolean existsByProductIdAndCustomerIdAndOrderId(Long productId, Long customerId, Long orderId);

    /**
     * 根据关键词搜索评价（标题、内容、产品名称）
     */
    @Query("SELECT pr FROM ProductReview pr JOIN Product p ON pr.productId = p.id " +
           "WHERE pr.title LIKE %:query% OR pr.content LIKE %:query% OR p.name LIKE %:query%")
    Page<ProductReview> searchReviewsByQuery(@Param("query") String query, Pageable pageable);

    /**
     * 根据状态和关键词搜索评价
     */
    @Query("SELECT pr FROM ProductReview pr JOIN Product p ON pr.productId = p.id " +
           "WHERE pr.status = :status AND (pr.title LIKE %:query% OR pr.content LIKE %:query% OR p.name LIKE %:query%)")
    Page<ProductReview> searchReviewsByStatusAndQuery(@Param("status") String status, @Param("query") String query, Pageable pageable);

    /**
     * 根据状态统计评价数量
     */
    Long countByStatus(String status);

    /**
     * 统计今日新增评价数量
     */
    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE DATE(pr.dateCreated) = CURRENT_DATE")
    Long countTodayReviews();

    /**
     * 计算所有产品的整体平均评分
     */
    @Query("SELECT COALESCE(1.0 * AVG(pr.rating), 0.0) FROM ProductReview pr WHERE pr.status = 'APPROVED'")
    BigDecimal getOverallAverageRating();

    /**
     * 根据日期范围计算平均评分
     */
    @Query("SELECT COALESCE(1.0 * AVG(pr.rating), 0.0) FROM ProductReview pr WHERE pr.dateCreated BETWEEN :startDate AND :endDate AND pr.status = 'APPROVED'")
    BigDecimal getAverageRatingByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
