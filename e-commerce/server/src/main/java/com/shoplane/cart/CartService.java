package com.shoplane.cart;

import com.shoplane.cart.dto.AddItemRequest;
import com.shoplane.cart.dto.CartDto;
import com.shoplane.common.ApiException;
import com.shoplane.product.Product;
import com.shoplane.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository carts;
    private final CartItemRepository items;
    private final ProductRepository products;

    public CartService(CartRepository carts, CartItemRepository items, ProductRepository products) {
        this.carts = carts;
        this.items = items;
        this.products = products;
    }

    private Cart ensureCart(Long userId) {
        return carts.findFirstByUserIdOrderByIdAsc(userId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUserId(userId);
            return carts.save(c);
        });
    }

    @Transactional
    public CartDto get(Long userId) {
        return CartDto.from(ensureCart(userId));
    }

    @Transactional
    public CartDto add(Long userId, AddItemRequest req) {
        Cart cart = ensureCart(userId);

        Product p = products.findByIdAndActiveTrue(req.productId())
                .orElseThrow(() -> ApiException.notFound("Product not available"));
        if (p.getStock() == null || p.getStock() < req.quantity()) {
            throw ApiException.conflict("Insufficient stock");
        }

        var existing = items.findFirstByCart_IdAndProduct_IdAndSizeAndColor(
                cart.getId(), p.getId(), req.size(), req.color());

        if (existing.isPresent()) {
            CartItem it = existing.get();
            it.setQty(it.getQty() + req.quantity());
            items.save(it);
        } else {
            CartItem it = new CartItem();
            it.setCart(cart);
            it.setProduct(p);
            it.setQty(req.quantity());
            it.setSize(req.size());
            it.setColor(req.color());
            cart.getItems().add(it);
            items.save(it);
        }
        return CartDto.from(carts.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public CartDto updateQty(Long userId, Long itemId, int qty) {
        CartItem it = items.findByIdAndCart_UserId(itemId, userId)
                .orElseThrow(() -> ApiException.notFound("Cart item not found"));
        if (qty <= 0) {
            items.delete(it);
        } else {
            it.setQty(qty);
            items.save(it);
        }
        return get(userId);
    }

    @Transactional
    public CartDto remove(Long userId, Long itemId) {
        items.findByIdAndCart_UserId(itemId, userId).ifPresent(items::delete);
        return get(userId);
    }

    @Transactional
    public CartDto clear(Long userId) {
        Cart cart = ensureCart(userId);
        cart.getItems().clear();
        carts.save(cart);
        return CartDto.from(cart);
    }
}
