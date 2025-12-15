package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * 根据购物车ID查找所有购物车项
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * 根据购物车ID和商品ID查找购物车项
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * 根据商品ID查找所有购物车项
     */
    List<CartItem> findByProductId(Long productId);

    /**
     * 删除购物车中的所有商品项
     */
    void deleteByCartId(Long cartId);

    /**
     * 删除指定购物车中的指定商品项
     */
    void deleteByCartIdAndProductId(Long cartId, Long productId);

    /**
     * 统计购物车中的商品项数量
     */
    long countByCartId(Long cartId);

    /**
     * 检查购物车中是否包含指定商品
     */
    boolean existsByCartIdAndProductId(Long cartId, Long productId);

    /**
     * 查找购物车中商品数量最多的商品项
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cartId = :cartId ORDER BY ci.quantity DESC")
    List<CartItem> findByCartIdOrderByQuantityDesc(@Param("cartId") Long cartId);

    /**
     * 根据购物车ID查找所有购物车项，并包含商品信息
     */
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.cartId = :cartId")
    List<CartItem> findByCartIdWithProduct(@Param("cartId") Long cartId);
}
