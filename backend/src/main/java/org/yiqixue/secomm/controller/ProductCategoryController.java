package org.yiqixue.secomm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.ApiResponse;
import org.yiqixue.secomm.dto.ProductCategoryDTO;
import org.yiqixue.secomm.service.ProductCategoryService;
import java.util.List;

/**
 * 商品分类控制器
 * 提供商品分类相关的REST API接口
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "分类管理", description = "商品分类相关的API接口")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    /**
     * 获取所有商品分类
     *
     * GET /api/categories
     */
    @GetMapping
    @Operation(summary = "获取所有分类", description = "获取系统中所有商品分类列表")
    public ResponseEntity<ApiResponse<List<ProductCategoryDTO>>> getAllCategories() {
        log.info("API调用 - 获取所有分类");

        List<ProductCategoryDTO> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    /**
     * 根据分类ID获取分类详情
     *
     * GET /api/categories/1
     */
    @GetMapping("/{categoryId}")
    @Operation(summary = "获取分类详情", description = "根据分类ID获取分类详细信息")
    public ResponseEntity<ApiResponse<ProductCategoryDTO>> getCategoryById(
            @Parameter(description = "分类ID")
            @PathVariable Long categoryId) {

        log.info("API调用 - 获取分类详情: categoryId={}", categoryId);

        ProductCategoryDTO category = categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(ApiResponse.success(category));
    }

    /**
     * 根据分类名称获取分类
     *
     * GET /api/categories/name/书籍
     */
    @GetMapping("/name/{categoryName}")
    @Operation(summary = "根据名称获取分类", description = "通过分类名称获取分类信息")
    public ResponseEntity<ApiResponse<ProductCategoryDTO>> getCategoryByName(
            @Parameter(description = "分类名称")
            @PathVariable String categoryName) {

        log.info("API调用 - 根据名称获取分类: categoryName={}", categoryName);

        ProductCategoryDTO category = categoryService.getCategoryByName(categoryName);

        return ResponseEntity.ok(ApiResponse.success(category));
    }
}