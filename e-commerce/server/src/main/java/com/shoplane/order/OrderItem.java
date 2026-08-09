package com.shoplane.order;

import com.shoplane.product.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, length = 200) private String name;

    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal price;

    @Column(nullable = false) private Integer qty;

    @Column(length = 32) private String size;
    @Column(length = 32) private String color;

    public Long getId() { return id; }
    public Order getOrder() { return order; }       public void setOrder(Order o) { this.order = o; }
    public Product getProduct() { return product; } public void setProduct(Product p) { this.product = p; }
    public String getName() { return name; }        public void setName(String n) { this.name = n; }
    public BigDecimal getPrice() { return price; }  public void setPrice(BigDecimal p) { this.price = p; }
    public Integer getQty() { return qty; }         public void setQty(Integer q) { this.qty = q; }
    public String getSize() { return size; }        public void setSize(String s) { this.size = s; }
    public String getColor() { return color; }      public void setColor(String c) { this.color = c; }
}
