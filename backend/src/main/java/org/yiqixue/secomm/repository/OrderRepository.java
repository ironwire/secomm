package org.yiqixue.secomm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Order;
import java.time.LocalDateTime;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据顾客ID查找订单（分页）
     */
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * 根据订单状态查找订单（分页）
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    /**
     * 根据订单状态查找订单（带客户信息）
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer WHERE o.status = :status")
    Page<Order> findByStatusWithCustomer(@Param("status") Order.OrderStatus status, Pageable pageable);

    /**
     * 获取所有订单（带客户信息）
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer")
    Page<Order> findAllWithCustomer(Pageable pageable);

    /**
     * 根据订单ID获取订单详情（带客户和订单项信息）
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findByIdWithCustomerAndItems(@Param("orderId") Long orderId);

    /**
     * 搜索订单（根据订单号、客户姓名、邮箱）
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer c WHERE " +
           "o.orderNumber LIKE %:query% OR " +
           "CONCAT(c.firstName, ' ', c.lastName) LIKE %:query% OR " +
           "c.email LIKE %:query%")
    Page<Order> searchOrders(@Param("query") String query, Pageable pageable);

    /**
     * 根据日期范围查找订单
     */
    @Query("SELECT o FROM Order o WHERE o.dateCreated BETWEEN :startDate AND :endDate")
    Page<Order> findOrdersByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * 统计各状态订单数量
     */
    long countByStatus(Order.OrderStatus status);

    /**
     * 统计今日订单数
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.dateCreated) = CURRENT_DATE")
    long countTodayOrders();

    /**
     * 统计本月订单数
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE YEAR(o.dateCreated) = YEAR(CURRENT_DATE) AND MONTH(o.dateCreated) = MONTH(CURRENT_DATE)")
    long countMonthOrders();

    /**
     * 根据日期范围获取总销售额
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.dateCreated BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED'")
    BigDecimal getTotalSalesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 根据日期范围统计订单数
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.dateCreated BETWEEN :startDate AND :endDate")
    Long countOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 获取订单状态分布
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.dateCreated BETWEEN :startDate AND :endDate GROUP BY o.status")
    List<Object[]> getOrderStatusDistribution(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 获取最近订单（带客户信息）
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer ORDER BY o.dateCreated DESC")
    List<Order> findRecentOrdersWithCustomer(Pageable pageable);

    /**
     * 获取畅销产品
     */
    @Query(value = "SELECT oi.product_id, " +
                   "SUM(oi.quantity), " +
                   "COALESCE(SUM(oi.unit_price * oi.quantity), 0.0) " +
                   "FROM order_items oi " +
                   "JOIN orders o ON o.id = oi.order_id " +
                   "WHERE o.date_created BETWEEN :startDate AND :endDate " +
                   "AND o.status != 'CANCELLED' " +
                   "GROUP BY oi.product_id " +
                   "ORDER BY SUM(oi.quantity) DESC " +
                   "LIMIT :limit", 
           nativeQuery = true)
    List<Object[]> getTopProductsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate, 
                                            @Param("limit") int limit);

    /**
     * 获取销售趋势数据
     */
    @Query("SELECT DATE(o.dateCreated), SUM(o.totalAmount), COUNT(o) " +
           "FROM Order o " +
           "WHERE o.dateCreated BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED' " +
           "GROUP BY DATE(o.dateCreated) " +
           "ORDER BY DATE(o.dateCreated)")
    List<Object[]> getSalesTrendByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 根据客户ID查找订单（带客户信息）
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer WHERE o.customerId = :customerId")
    Page<Order> findByCustomerIdWithCustomer(@Param("customerId") Long customerId, Pageable pageable);

    /**
     * 根据订单ID和客户ID获取订单详情（带客户和订单项信息）
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId AND o.customerId = :customerId")
    Optional<Order> findByIdAndCustomerIdWithItems(@Param("orderId") Long orderId, @Param("customerId") Long customerId);

    /**
     * 根据客户ID和状态查找订单（带客户信息）
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer WHERE o.customerId = :customerId AND o.status = :status")
    Page<Order> findByCustomerIdAndStatusWithCustomer(@Param("customerId") Long customerId, @Param("status") Order.OrderStatus status, Pageable pageable);
}
