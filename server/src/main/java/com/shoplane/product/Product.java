package com.shoplane.product;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40) private String sku;
    @Column(nullable = false, length = 200)               private String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "sub_category", length = 80) private String subCategory;

    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal price;
    @Column(name = "old_price", precision = 10, scale = 2) private BigDecimal oldPrice;

    @Column(precision = 3, scale = 2) private BigDecimal rating;
    @Column(name = "review_count")    private Integer reviewCount;
    @Column(nullable = false)         private Integer stock;
    @Column(name = "image_url", length = 255) private String imageUrl;

    @Column(columnDefinition = "TEXT") private String description;

    @Column(name = "is_active", nullable = false) private Boolean active = true;

    @Column(name = "created_at", insertable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false) private Instant updatedAt;

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public String getSubCategory() { return subCategory; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getOldPrice() { return oldPrice; }
    public BigDecimal getRating() { return rating; }
    public Integer getReviewCount() { return reviewCount; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public Boolean getActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
