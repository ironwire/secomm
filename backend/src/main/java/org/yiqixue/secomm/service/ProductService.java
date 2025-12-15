package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.dto.PageResponse;
import org.yiqixue.secomm.dto.ProductDTO;
import org.yiqixue.secomm.entity.Product;
import org.yiqixue.secomm.exception.ResourceNotFoundException;
import org.yiqixue.secomm.mapper.ProductMapper;
import org.yiqixue.secomm.repository.ProductRepository;
import org.springframework.web.multipart.MultipartFile;
import org.yiqixue.secomm.entity.ProductCategory;
import org.yiqixue.secomm.repository.ProductCategoryRepository;
import org.yiqixue.secomm.exception.BusinessException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

/**
 * 商品服务层
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductCategoryRepository categoryRepository;

    /**
     * 获取所有激活状态的商品（分页）
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向（asc/desc）
     * @return 分页商品数据
     */
    public PageResponse<ProductDTO> getAllActiveProducts(
            int page, int size, String sortBy, String sortDir) {

        log.info("获取所有激活商品 - 页码: {}, 大小: {}, 排序: {} {}",
                page, size, sortBy, sortDir);

        // 创建排序对象
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        // 创建分页对象
        Pageable pageable = PageRequest.of(page, size, sort);

        // 查询激活状态的商品
        Page<Product> productPage = productRepository.findByActiveTrue(pageable);

        // 转换为DTO并返回
        return productMapper.toPageResponse(productPage);
    }

    /**
     * 根据分类ID获取商品（分页）
     *
     * @param categoryId 分类ID
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页商品数据
     */
    public PageResponse<ProductDTO> getProductsByCategory(
            Long categoryId, int page, int size, String sortBy, String sortDir) {

        log.info("根据分类获取商品 - 分类ID: {}, 页码: {}, 大小: {}",
                categoryId, page, size);

        // 创建排序和分页对象
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 查询该分类下的激活商品
        Page<Product> productPage = productRepository
                .findByCategoryIdAndActiveTrue(categoryId, pageable);

        // 如果该分类没有商品，记录日志
        if (productPage.isEmpty()) {
            log.warn("分类ID {} 下没有找到商品", categoryId);
        }

        return productMapper.toPageResponse(productPage);
    }

    /**
     * 根据商品ID获取商品详情
     *
     * @param productId 商品ID
     * @return 商品DTO
     */
    public ProductDTO getProductById(Long productId) {
        log.info("获取商品详情 - 商品ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "id", productId));

        return productMapper.toDTO(product);
    }

    /**
     * 搜索商品（根据名称或描述）
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 分页商品数据
     */
    public PageResponse<ProductDTO> searchProducts(
            String keyword, int page, int size) {

        log.info("搜索商品 - 关键词: {}, 页码: {}, 大小: {}", keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productRepository
                .searchProducts(keyword, pageable);

        return productMapper.toPageResponse(productPage);
    }

    /**
     * 根据SKU获取商品
     *
     * @param sku 商品SKU
     * @return 商品DTO
     */
    public ProductDTO getProductBySku(String sku) {
        log.info("根据SKU获取商品 - SKU: {}", sku);

        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "sku", sku));

        return productMapper.toDTO(product);
    }

    /**
     * 创建新商品（带图片上传）
     */
    @Transactional
    public ProductDTO createProduct(String sku, String name, String description, 
                                   BigDecimal unitPrice, Integer unitsInStock, 
                                   Long categoryId, Boolean active, MultipartFile image) {
        
        log.info("创建新商品 - SKU: {}, 名称: {}, 分类ID: {}", sku, name, categoryId);

        // 检查SKU是否已存在
        if (productRepository.existsBySku(sku)) {
            throw new BusinessException("SKU已存在: " + sku);
        }

        // 验证分类是否存在
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "id", categoryId));

        String imageUrl = null;
        
        // 处理图片上传
        if (image != null && !image.isEmpty()) {
            imageUrl = handleImageUpload(image, sku, category.getCategoryEnglishName());
        }

        // 创建商品实体
        Product product = Product.builder()
                .sku(sku)
                .name(name)
                .description(description)
                .unitPrice(unitPrice)
                .unitsInStock(unitsInStock)
                .active(active != null ? active : true)
                .imageUrl(imageUrl)
                .category(category)
                .build();

        product = productRepository.save(product);
        
        log.info("商品创建成功 - ID: {}, SKU: {}", product.getId(), product.getSku());
        
        return productMapper.toDTO(product);
    }

    /**
     * 处理图片上传
     */
    private String handleImageUpload(MultipartFile image, String sku, String categoryEnglishName) {
        try {
            // 图片格式校验
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BusinessException("只支持图片文件上传");
            }
            
            // 支持的图片格式
            List<String> allowedTypes = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif");
            if (!allowedTypes.contains(contentType.toLowerCase())) {
                throw new BusinessException("只支持 JPEG、PNG、GIF 格式的图片");
            }
            
            // 文件大小校验 (10MB)
            if (image.getSize() > 10 * 1024 * 1024) {
                throw new BusinessException("图片文件大小不能超过10MB");
            }
            
            // 获取文件扩展名
            String originalFilename = image.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 生成随机码
            String randomCode = UUID.randomUUID().toString().substring(0, 8);
            String filename = sku + "_" + randomCode + extension;
            
            // 创建目录结构: /uploads/images/products/{category}/
            String categoryDir = categoryEnglishName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
            Path uploadDir = Paths.get("uploads", "images", "products", categoryDir);
            
            // 确保目录存在
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 返回相对路径作为URL
            String imageUrl = "/uploads/images/products/" + categoryDir + "/" + filename;
            
            log.info("图片上传成功 - 文件路径: {}", filePath.toString());
            
            return imageUrl;
            
        } catch (IOException e) {
            log.error("图片上传失败: {}", e.getMessage(), e);
            throw new BusinessException("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除商品
     *
     * @param productId 商品ID
     */
    @Transactional
    public void deleteProduct(Long productId) {
        log.info("删除商品 - 商品ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "id", productId));

        // 删除商品图片文件（如果存在）
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                deleteImageFile(product.getImageUrl());
            } catch (Exception e) {
                log.warn("删除商品图片文件失败: {}", e.getMessage());
            }
        }

        productRepository.delete(product);
        
        log.info("商品删除成功 - ID: {}, SKU: {}", product.getId(), product.getSku());
    }

    /**
     * 删除图片文件
     */
    private void deleteImageFile(String imageUrl) {
        try {
            // 从URL中提取文件路径
            if (imageUrl.startsWith("/uploads/")) {
                String fileName = imageUrl.substring("/uploads/".length());
                Path filePath = Paths.get("uploads", fileName);
                
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("图片文件删除成功: {}", filePath);
                }
            }
        } catch (IOException e) {
            log.error("删除图片文件失败: {}", e.getMessage());
            throw new BusinessException("删除图片文件失败");
        }
    }

    /**
     * 更新商品
     */
    @Transactional
    public ProductDTO updateProduct(Long productId, String sku, String name, String description, 
                                   BigDecimal unitPrice, Integer unitsInStock, 
                                   Long categoryId, Boolean active, MultipartFile image) {
        
        log.info("更新商品 - ID: {}, SKU: {}, 名称: {}", productId, sku, name);

        // 查找现有商品
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // 检查SKU是否被其他商品使用
        if (!existingProduct.getSku().equals(sku) && productRepository.existsBySku(sku)) {
            throw new BusinessException("SKU已存在: " + sku);
        }

        // 验证分类是否存在
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "id", categoryId));

        String imageUrl = existingProduct.getImageUrl(); // 保持原有图片URL
        
        // 处理新图片上传
        if (image != null && !image.isEmpty()) {
            // 删除旧图片文件
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    deleteImageFile(imageUrl);
                } catch (Exception e) {
                    log.warn("删除旧图片文件失败: {}", e.getMessage());
                }
            }
            
            // 上传新图片
            imageUrl = handleImageUpload(image, sku, category.getCategoryEnglishName());
        }

        // 更新商品信息
        existingProduct.setSku(sku);
        existingProduct.setName(name);
        existingProduct.setDescription(description);
        existingProduct.setUnitPrice(unitPrice);
        existingProduct.setUnitsInStock(unitsInStock);
        existingProduct.setActive(active != null ? active : true);
        existingProduct.setImageUrl(imageUrl);
        existingProduct.setCategory(category);

        Product updatedProduct = productRepository.save(existingProduct);
        
        log.info("商品更新成功 - ID: {}, SKU: {}", updatedProduct.getId(), updatedProduct.getSku());
        
        return productMapper.toDTO(updatedProduct);
    }
}
