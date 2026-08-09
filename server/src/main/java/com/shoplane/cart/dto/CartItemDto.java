package com.shoplane.cart.dto;

import com.shoplane.cart.CartItem;

import java.math.BigDecimal;
import java.time.Instant;

public record CartItemDto(
        Long id,
        Long productId,
        Integer qty,
        String size,
        String color,
        Instant addedAt,
        Product product,
        BigDecimal lineTotal) {

    public record Product(Long id, String sku, String name, BigDecimal price,
                          BigDecimal oldPrice, String imageUrl, Integer stock) {}

    public static CartItemDto from(CartItem i) {
        var p = i.getProduct();
        BigDecimal line = p.getPrice().multiply(BigDecimal.valueOf(i.getQty()));
        return new CartItemDto(
                i.getId(), p.getId(), i.getQty(), i.getSize(), i.getColor(), i.getAddedAt(),
                new Product(p.getId(), p.getSku(), p.getName(), p.getPrice(),
                        p.getOldPrice(), p.getImageUrl(), p.getStock()),
                line);
    }
}
