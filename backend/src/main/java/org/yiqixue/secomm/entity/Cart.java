package org.yiqixue.secomm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "cart", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_customer_id", columnNames = "customer_id")
       },
       indexes = {
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_last_updated", columnList = "last_updated")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CartStatus status = CartStatus.ACTIVE;

    @Column(name = "total_price", precision = 19, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "total_quantity")
    private Integer totalQuantity = 0;

    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CartItem> cartItems;

    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    /**
     * 购物车状态枚举
     */
    public enum CartStatus {
        ACTIVE("活跃"),
        ABANDONED("已放弃"),
        CONVERTED("已转换为订单");

        private final String description;

        CartStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 便捷方法：添加购物车项
     */
    public void addCartItem(CartItem cartItem) {
        if (cartItems != null) {
            cartItems.add(cartItem);
            cartItem.setCart(this);
        }
    }

    /**
     * 便捷方法：移除购物车项
     */
    public void removeCartItem(CartItem cartItem) {
        if (cartItems != null) {
            cartItems.remove(cartItem);
            cartItem.setCart(null);
        }
    }

    /**
     * 便捷方法：计算总价格
     */
    public void calculateTotals() {
        if (cartItems == null || cartItems.isEmpty()) {
            this.totalPrice = BigDecimal.ZERO;
            this.totalQuantity = 0;
            return;
        }

        this.totalPrice = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalQuantity = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}