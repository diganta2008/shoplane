package com.shoplane.cart;

import com.shoplane.product.Product;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false) private Integer qty;

    @Column(length = 32) private String size;
    @Column(length = 32) private String color;

    @Column(name = "added_at", insertable = false, updatable = false) private Instant addedAt;

    public Long getId() { return id; }
    public Cart getCart() { return cart; }         public void setCart(Cart cart) { this.cart = cart; }
    public Product getProduct() { return product; } public void setProduct(Product p) { this.product = p; }
    public Integer getQty() { return qty; }        public void setQty(Integer qty) { this.qty = qty; }
    public String getSize() { return size; }       public void setSize(String size) { this.size = size; }
    public String getColor() { return color; }     public void setColor(String color) { this.color = color; }
    public Instant getAddedAt() { return addedAt; }
}
