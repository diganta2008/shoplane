package com.shoplane.wishlist;

import com.shoplane.product.Product;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "wishlists")
public class Wishlist {

    @EmbeddedId
    private WishlistId id;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "added_at", insertable = false, updatable = false)
    private Instant addedAt;

    public Wishlist() {}
    public Wishlist(WishlistId id, Product product) {
        this.id = id;
        this.product = product;
    }

    public WishlistId getId() { return id; }
    public Product getProduct() { return product; }
    public Instant getAddedAt() { return addedAt; }
}
