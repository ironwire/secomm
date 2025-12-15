package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.ProductCategory;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    /**
     * 根据分类名称查找分类
     */
    Optional<ProductCategory> findByCategoryName(String categoryName);

    /**
     * 检查分类名称是否存在
     */
    boolean existsByCategoryName(String categoryName);
}