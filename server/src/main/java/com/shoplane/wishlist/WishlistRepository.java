package com.shoplane.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {
    List<Wishlist> findAllByIdUserIdOrderByAddedAtDesc(Long userId);
    void deleteByIdUserIdAndIdProductId(Long userId, Long productId);
}
