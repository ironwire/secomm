package org.yiqixue.secomm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.ApiResponse;
import org.yiqixue.secomm.dto.PageResponse;
import org.yiqixue.secomm.dto.ProductDTO;
import org.yiqixue.secomm.dto.ProductUpdateRequest;
import org.yiqixue.secomm.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

/**
 * 商品控制器
 * 提供商品相关的REST API接口
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "商品管理", description = "商品相关的API接口")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;

    /**
     * 获取所有激活商品（分页）
     *
     * GET /api/products?page=0&size=10&sortBy=name&sortDir=asc
     */
    @GetMapping
    @Operation(summary = "获取所有商品", description = "分页获取所有激活状态的商品")
    public ResponseEntity<ApiResponse<PageResponse<ProductDTO>>> getAllProducts(
            @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "排序字段")
            @RequestParam(defaultValue = "name") String sortBy,

            @Parameter(description = "排序方向：asc或desc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("API调用 - 获取所有商品: page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        PageResponse<ProductDTO> products = productService
                .getAllActiveProducts(page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /**
     * 根据分类ID获取商品（分页）
     *
     * GET /api/products/category/1?page=0&size=10
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "按分类获取商品", description = "根据分类ID分页获取商品列表")
    public ResponseEntity<ApiResponse<PageResponse<ProductDTO>>> getProductsByCategory(
            @Parameter(description = "分类ID")
            @PathVariable Long categoryId,

            @Parameter(description = "页码")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "排序字段")
            @RequestParam(defaultValue = "name") String sortBy,

            @Parameter(description = "排序方向")
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("API调用 - 按分类获取商品: categoryId={}, page={}, size={}",
                categoryId, page, size);

        PageResponse<ProductDTO> products = productService
                .getProductsByCategory(categoryId, page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /**
     * 根据商品ID获取商品详情
     *
     * GET /api/products/123
     */
    @GetMapping("/{productId}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详细信息")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
            @Parameter(description = "商品ID")
            @PathVariable Long productId) {

        log.info("API调用 - 获取商品详情: productId={}", productId);

        ProductDTO product = productService.getProductById(productId);

        return ResponseEntity.ok(ApiResponse.success(product));
    }

    /**
     * 搜索商品
     *
     * GET /api/products/search?keyword=coffee&page=0&size=10
     */
    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "根据关键词搜索商品名称或描述")
    public ResponseEntity<ApiResponse<PageResponse<ProductDTO>>> searchProducts(
            @Parameter(description = "搜索关键词")
            @RequestParam String keyword,

            @Parameter(description = "页码")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size) {

        log.info("API调用 - 搜索商品: keyword={}, page={}, size={}",
                keyword, page, size);

        PageResponse<ProductDTO> products = productService
                .searchProducts(keyword, page, size);

        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /**
     * 根据SKU获取商品
     *
     * GET /api/products/sku/BOOK-001
     */
    @GetMapping("/sku/{sku}")
    @Operation(summary = "根据SKU获取商品", description = "通过商品SKU获取商品信息")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductBySku(
            @Parameter(description = "商品SKU")
            @PathVariable String sku) {

        log.info("API调用 - 根据SKU获取商品: sku={}", sku);

        ProductDTO product = productService.getProductBySku(sku);

        return ResponseEntity.ok(ApiResponse.success(product));
    }

    /**
     * 创建新商品（带图片上传）
     *
     * POST /api/products
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "创建新商品", description = "创建新商品，支持图片上传")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @Parameter(description = "商品SKU")
            @RequestParam String sku,
            
            @Parameter(description = "商品名称")
            @RequestParam String name,
            
            @Parameter(description = "商品描述")
            @RequestParam(required = false) String description,
            
            @Parameter(description = "单价")
            @RequestParam BigDecimal unitPrice,
            
            @Parameter(description = "库存数量")
            @RequestParam Integer unitsInStock,
            
            @Parameter(description = "分类ID")
            @RequestParam Long categoryId,
            
            @Parameter(description = "是否激活")
            @RequestParam(defaultValue = "true") Boolean active,
            
            @Parameter(description = "商品图片")
            @RequestParam(required = false) MultipartFile image) {

        log.info("API调用 - 创建新商品: sku={}, name={}, categoryId={}", sku, name, categoryId);

        // 输入校验
        if (sku == null || sku.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "SKU不能为空"));
        }
        
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "商品名称不能为空"));
        }
        
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "单价不能为空且必须大于等于0"));
        }
        
        if (unitsInStock == null || unitsInStock < 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "库存数量不能为空且必须大于等于0"));
        }

        try {
            ProductDTO product = productService.createProduct(
                    sku, name, description, unitPrice, unitsInStock, 
                    categoryId, active, image);
            
            return ResponseEntity.ok(ApiResponse.success("商品创建成功", product));
            
        } catch (Exception e) {
            log.error("创建商品失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 删除商品
     *
     * DELETE /api/products/123
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "删除商品", description = "根据商品ID删除商品")
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @Parameter(description = "商品ID")
            @PathVariable Long productId) {

        log.info("API调用 - 删除商品: productId={}", productId);

        try {
            productService.deleteProduct(productId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "商品删除成功"));
            
        } catch (Exception e) {
            log.error("删除商品失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 更新商品（带图片上传）
     *
     * PUT /api/products/123
     */
    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "更新商品", description = "更新商品信息，支持图片上传")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @Parameter(description = "商品ID")
            @PathVariable Long productId,
            
            @Parameter(description = "商品SKU")
            @RequestParam String sku,
            
            @Parameter(description = "商品名称")
            @RequestParam String name,
            
            @Parameter(description = "商品描述")
            @RequestParam(required = false) String description,
            
            @Parameter(description = "单价")
            @RequestParam BigDecimal unitPrice,
            
            @Parameter(description = "库存数量")
            @RequestParam Integer unitsInStock,
            
            @Parameter(description = "分类ID")
            @RequestParam Long categoryId,
            
            @Parameter(description = "是否激活")
            @RequestParam(defaultValue = "true") Boolean active,
            
            @Parameter(description = "商品图片")
            @RequestParam(required = false) MultipartFile image) {

        log.info("API调用 - 更新商品: productId={}, sku={}, name={}", productId, sku, name);

        try {
            ProductDTO product = productService.updateProduct(
                    productId, sku, name, description, unitPrice, unitsInStock, 
                    categoryId, active, image);
            
            return ResponseEntity.ok(ApiResponse.success("商品更新成功", product));
            
        } catch (Exception e) {
            log.error("更新商品失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 更新商品（JSON格式，不包含图片）
     *
     * PUT /api/products/123
     */
    @PutMapping(value = "/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "更新商品信息", description = "更新商品基本信息（不包含图片）")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProductJson(
            @Parameter(description = "商品ID")
            @PathVariable Long productId,
            
            @RequestBody @Valid ProductUpdateRequest request) {

        log.info("API调用 - 更新商品(JSON): productId={}, sku={}", productId, request.getSku());

        try {
            ProductDTO product = productService.updateProduct(
                    productId, request.getSku(), request.getName(), request.getDescription(),
                    request.getUnitPrice(), request.getUnitsInStock(), 
                    request.getCategoryId(), request.getActive(), null);
            
            return ResponseEntity.ok(ApiResponse.success("商品更新成功", product));
            
        } catch (Exception e) {
            log.error("更新商品失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
