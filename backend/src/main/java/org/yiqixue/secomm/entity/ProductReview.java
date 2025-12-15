package org.yiqixue.secomm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评价实体类
 * 对应数据库表: product_review
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "product_review",
       indexes = {
           @Index(name = "idx_product_id", columnList = "product_id"),
           @Index(name = "idx_customer_id", columnList = "customer_id"),
           @Index(name = "idx_order_id", columnList = "order_id"),
           @Index(name = "idx_rating", columnList = "rating"),
           @Index(name = "idx_status", columnList = "status")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品ID (外键)
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * 客户ID (外键)
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * 订单ID (外键)
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 评分 (1-5星)
     */
    @Column(name = "rating", nullable = false)
    @Min(value = 1, message = "评分最低为1星")
    @Max(value = 5, message = "评分最高为5星")
    private Integer rating;

    /**
     * 评价标题
     */
    @Column(name = "title")
    private String title;

    /**
     * 评价内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 有帮助的数量
     */
    @Column(name = "helpful_count", nullable = false)
    private Integer helpfulCount = 0;

    /**
     * 是否认证购买 (数据库BIT(1)类型)
     */
    @Column(name = "verified_purchase", nullable = false, columnDefinition = "BIT(1)")
    private Boolean verifiedPurchase = false;

    /**
     * 评价状态 (PENDING, APPROVED, REJECTED)
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "PENDING";

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "date_created", nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime dateCreated;

    /**
     * 最后更新时间
     */
    @LastModifiedDate
    @Column(name = "last_updated", columnDefinition = "DATETIME(6)")
    private LocalDateTime lastUpdated;

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_review_product"))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_review_customer"))
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_review_order"))
    private Order order;

    @PrePersist
    protected void onCreate() {
        if (dateCreated == null) {
            dateCreated = LocalDateTime.now();
        }
        if (lastUpdated == null) {
            lastUpdated = LocalDateTime.now();
        }
        if (helpfulCount == null) {
            helpfulCount = 0;
        }
        if (verifiedPurchase == null) {
            verifiedPurchase = false;
        }
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ProductReview{" +
                "id=" + id +
                ", productId=" + productId +
                ", customerId=" + customerId +
                ", orderId=" + orderId +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", helpfulCount=" + helpfulCount +
                ", verifiedPurchase=" + verifiedPurchase +
                ", status='" + status + '\'' +
                ", dateCreated=" + dateCreated +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
