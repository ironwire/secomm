package org.yiqixue.secomm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_item",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"})
       },
       indexes = {
           @Index(name = "idx_cart_id", columnList = "cart_id"),
           @Index(name = "idx_product_id", columnList = "product_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cart_id", nullable = false)
    private Long cartId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", precision = 19, scale = 2, insertable = false, updatable = false)
    private BigDecimal subtotal;

    @Column(name = "date_added", nullable = false, updatable = false)
    private LocalDateTime dateAdded;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Product product;

    @PrePersist
    protected void onCreate() {
        dateAdded = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        calculateSubtotal();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
        calculateSubtotal();
    }

    /**
     * 计算小计（由于使用了数据库生成列，这里主要用于业务逻辑）
     */
    public void calculateSubtotal() {
        if (quantity != null && unitPrice != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * 获取计算后的小计
     */
    public BigDecimal getSubtotal() {
        if (subtotal == null && quantity != null && unitPrice != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return subtotal;
    }

    /**
     * 便捷方法：增加数量
     */
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
            calculateSubtotal();
        }
    }

    /**
     * 便捷方法：减少数量
     */
    public void decreaseQuantity(int amount) {
        if (amount > 0 && this.quantity > amount) {
            this.quantity -= amount;
            calculateSubtotal();
        }
    }

    /**
     * 便捷方法：设置数量
     */
    public void setQuantity(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.quantity = quantity;
            calculateSubtotal();
        }
    }
}