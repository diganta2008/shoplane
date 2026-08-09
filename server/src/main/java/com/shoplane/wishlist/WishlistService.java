package com.shoplane.wishlist;

import com.shoplane.common.ApiException;
import com.shoplane.product.Product;
import com.shoplane.product.ProductRepository;
import com.shoplane.wishlist.dto.WishlistEntryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlists;
    private final ProductRepository products;

    public WishlistService(WishlistRepository wishlists, ProductRepository products) {
        this.wishlists = wishlists;
        this.products = products;
    }

    @Transactional(readOnly = true)
    public List<WishlistEntryDto> list(Long userId) {
        return wishlists.findAllByIdUserIdOrderByAddedAtDesc(userId)
                .stream().map(WishlistEntryDto::from).toList();
    }

    @Transactional
    public void add(Long userId, Long productId) {
        Product p = products.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> ApiException.notFound("Product not available"));
        WishlistId id = new WishlistId(userId, productId);
        if (wishlists.existsById(id)) return;
        wishlists.save(new Wishlist(id, p));
    }

    @Transactional
    public void remove(Long userId, Long productId) {
        wishlists.deleteByIdUserIdAndIdProductId(userId, productId);
    }
}
