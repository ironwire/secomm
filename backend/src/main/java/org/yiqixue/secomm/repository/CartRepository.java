package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Cart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 根据顾客ID查找购物车
     */
    Optional<Cart> findByCustomerId(Long customerId);

    /**
     * 根据顾客ID和状态查找购物车
     */
    Optional<Cart> findByCustomerIdAndStatus(Long customerId, Cart.CartStatus status);

    /**
     * 查找活跃状态的购物车
     */
    List<Cart> findByStatus(Cart.CartStatus status);

    /**
     * 查找指定时间之前最后更新的购物车（用于清理废弃购物车）
     */
    @Query("SELECT c FROM Cart c WHERE c.lastUpdated < :cutoffTime AND c.status = :status")
    List<Cart> findCartsLastUpdatedBefore(@Param("cutoffTime") LocalDateTime cutoffTime, 
                                         @Param("status") Cart.CartStatus status);

    /**
     * 统计顾客的购物车数量
     */
    long countByCustomerId(Long customerId);

    /**
     * 检查顾客是否有活跃的购物车
     */
    boolean existsByCustomerIdAndStatus(Long customerId, Cart.CartStatus status);
}