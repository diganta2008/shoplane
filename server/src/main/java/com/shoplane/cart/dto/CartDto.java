package com.shoplane.cart.dto;

import com.shoplane.cart.Cart;
import com.shoplane.cart.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record CartDto(Long cartId, List<CartItemDto> items, BigDecimal subtotal, Integer itemCount) {

    public static CartDto from(Cart cart) {
        List<CartItemDto> items = cart.getItems().stream().map(CartItemDto::from).toList();
        BigDecimal subtotal = items.stream()
                .map(CartItemDto::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        int count = cart.getItems().stream().mapToInt(CartItem::getQty).sum();
        return new CartDto(cart.getId(), items, subtotal, count);
    }
}
