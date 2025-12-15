package org.yiqixue.secomm.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.yiqixue.secomm.dto.PageResponse;
import org.yiqixue.secomm.dto.ProductCategoryDTO;
import org.yiqixue.secomm.dto.ProductDTO;
import org.yiqixue.secomm.entity.Product;
import org.yiqixue.secomm.entity.ProductCategory;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entity 和 DTO 之间的转换工具类
 */
@Component
public class ProductMapper {

    /**
     * Product Entity 转 DTO
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        return ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .unitPrice(product.getUnitPrice())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .unitsInStock(product.getUnitsInStock())
                .dateCreated(product.getDateCreated())
                .lastUpdated(product.getLastUpdated())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getCategoryName())
                .build();
    }

    /**
     * Product Entity List 转 DTO List
     */
    public List<ProductDTO> toDTOList(List<Product> products) {
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Product Page 转 PageResponse
     */
    public PageResponse<ProductDTO> toPageResponse(Page<Product> page) {
        List<ProductDTO> dtoList = toDTOList(page.getContent());

        return PageResponse.<ProductDTO>builder()
                .content(dtoList)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }

    /**
     * ProductCategory Entity 转 DTO
     */
    public ProductCategoryDTO toCategoryDTO(ProductCategory category) {
        if (category == null) {
            return null;
        }

        return ProductCategoryDTO.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .categoryEnglishName(category.getCategoryEnglishName())
                .productCount(category.getProducts() != null ?
                        (long) category.getProducts().size() : 0L)
                .build();
    }

    /**
     * ProductCategory Entity List 转 DTO List
     */
    public List<ProductCategoryDTO> toCategoryDTOList(List<ProductCategory> categories) {
        return categories.stream()
                .map(this::toCategoryDTO)
                .collect(Collectors.toList());
    }
}