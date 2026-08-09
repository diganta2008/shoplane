package com.shoplane.wishlist.dto;

import com.shoplane.product.Product;
import com.shoplane.wishlist.Wishlist;

import java.math.BigDecimal;
import java.time.Instant;

public record WishlistEntryDto(Long productId, Instant addedAt, ProductSummary product) {

    public record ProductSummary(Long id, String sku, String name, BigDecimal price,
                                 BigDecimal oldPrice, BigDecimal rating, Integer reviewCount,
                                 Integer stock, boolean inStock, String imageUrl, Category category) {
        public record Category(String slug, String name) {}
    }

    public static WishlistEntryDto from(Wishlist w) {
        Product p = w.getProduct();
        var cat = p.getCategory();
        return new WishlistEntryDto(
                p.getId(),
                w.getAddedAt(),
                new ProductSummary(p.getId(), p.getSku(), p.getName(), p.getPrice(), p.getOldPrice(),
                        p.getRating(), p.getReviewCount(),
                        p.getStock(), p.getStock() != null && p.getStock() > 0,
                        p.getImageUrl(),
                        new ProductSummary.Category(cat.getSlug(), cat.getName())));
    }
}
