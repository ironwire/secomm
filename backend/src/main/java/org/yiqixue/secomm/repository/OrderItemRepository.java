package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.OrderItem;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 根据订单ID查找所有订单项
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 根据商品ID查找所有订单项
     */
    List<OrderItem> findByProductId(Long productId);

    /**
     * 查找某个商品的总销售数量
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.productId = :productId")
    Long getTotalQuantitySoldByProduct(@Param("productId") Long productId);

    /**
     * 查找最畅销的商品
     */
    @Query("SELECT oi.productId, SUM(oi.quantity) as totalSold " +
            "FROM OrderItem oi " +
            "GROUP BY oi.productId " +
            "ORDER BY totalSold DESC")
    List<Object[]> findBestSellingProducts();
}