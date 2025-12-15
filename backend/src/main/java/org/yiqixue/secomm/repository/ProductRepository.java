package org.yiqixue.secomm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Product;
import org.yiqixue.secomm.entity.ProductCategory;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据分类查找商品（分页）
     */
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

    /**
     * 根据分类ID查找商品（分页）
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 根据SKU查找商品
     */
    Optional<Product> findBySku(String sku);

    /**
     * 查找激活状态的商品（分页）
     */
    Page<Product> findByActiveTrue(Pageable pageable);

    /**
     * 根据分类ID和激活状态查找商品（分页）
     */
    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    /**
     * 根据名称模糊搜索商品（分页）
     */
    Page<Product> findByNameContaining(String name, Pageable pageable);

    /**
     * 根据名称或描述搜索商品（分页）
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查找库存不足的商品
     */
    @Query("SELECT p FROM Product p WHERE p.unitsInStock < :threshold")
    Page<Product> findLowStockProducts(@Param("threshold") Integer threshold, Pageable pageable);

    boolean existsBySku(String sku);

    /**
     * 统计低库存产品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.unitsInStock < :threshold AND p.active = true")
    Long countLowStockProducts(@Param("threshold") Integer threshold);
}
