package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.dto.ProductCategoryDTO;
import org.yiqixue.secomm.entity.ProductCategory;
import org.yiqixue.secomm.exception.ResourceNotFoundException;
import org.yiqixue.secomm.mapper.ProductMapper;
import org.yiqixue.secomm.repository.ProductCategoryRepository;
import java.util.List;

/**
 * 商品分类服务层
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    /**
     * 获取所有商品分类
     *
     * @return 分类DTO列表
     */
    public List<ProductCategoryDTO> getAllCategories() {
        log.info("获取所有商品分类");

        List<ProductCategory> categories = categoryRepository.findAll();

        return productMapper.toCategoryDTOList(categories);
    }

    /**
     * 根据ID获取分类详情
     *
     * @param categoryId 分类ID
     * @return 分类DTO
     */
    public ProductCategoryDTO getCategoryById(Long categoryId) {
        log.info("获取分类详情 - 分类ID: {}", categoryId);

        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ProductCategory", "id", categoryId));

        return productMapper.toCategoryDTO(category);
    }

    /**
     * 根据分类名称获取分类
     *
     * @param categoryName 分类名称
     * @return 分类DTO
     */
    public ProductCategoryDTO getCategoryByName(String categoryName) {
        log.info("根据名称获取分类 - 名称: {}", categoryName);

        ProductCategory category = categoryRepository
                .findByCategoryName(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ProductCategory", "name", categoryName));

        return productMapper.toCategoryDTO(category);
    }
}