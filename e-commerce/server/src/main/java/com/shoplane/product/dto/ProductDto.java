package com.shoplane.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shoplane.product.Product;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductDto(
        Long id,
        String sku,
        String name,
        CategoryDto category,
        String subCategory,
        BigDecimal price,
        BigDecimal oldPrice,
        BigDecimal rating,
        Integer reviewCount,
        Integer stock,
        boolean inStock,
        String imageUrl,
        String description,
        Instant createdAt,
        Instant updatedAt) {

    public static ProductDto from(Product p) {
        return new ProductDto(
                p.getId(), p.getSku(), p.getName(),
                CategoryDto.from(p.getCategory()),
                p.getSubCategory(),
                p.getPrice(), p.getOldPrice(),
                p.getRating(), p.getReviewCount(),
                p.getStock(), p.getStock() != null && p.getStock() > 0,
                p.getImageUrl(), p.getDescription(),
                p.getCreatedAt(), p.getUpdatedAt());
    }
}
