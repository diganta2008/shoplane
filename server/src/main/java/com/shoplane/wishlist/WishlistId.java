package com.shoplane.wishlist;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WishlistId implements Serializable {

    private Long userId;
    private Long productId;

    public WishlistId() {}

    public WishlistId(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public Long getUserId() { return userId; }
    public Long getProductId() { return productId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WishlistId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() { return Objects.hash(userId, productId); }
}
