package com.shoplane.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByIdAndCart_UserId(Long id, Long userId);

    Optional<CartItem> findFirstByCart_IdAndProduct_IdAndSizeAndColor(
            Long cartId, Long productId, String size, String color);
}
