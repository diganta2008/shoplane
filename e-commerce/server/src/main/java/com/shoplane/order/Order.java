package com.shoplane.order;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 32)
    private String orderNumber;

    @Column(name = "user_id") private Long userId;

    @Column(nullable = false, length = 20) private String status;

    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal subtotal;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal discount;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal shipping;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal tax;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal total;

    @Column(name = "coupon_code", length = 32) private String couponCode;

    @Column(name = "payment_method", nullable = false, length = 10) private String paymentMethod;

    @Column(name = "ship_name",   nullable = false, length = 120) private String shipName;
    @Column(name = "ship_email",  nullable = false, length = 190) private String shipEmail;
    @Column(name = "ship_phone",  nullable = false, length = 32)  private String shipPhone;
    @Column(name = "ship_address",nullable = false, length = 255) private String shipAddress;
    @Column(name = "ship_city",   nullable = false, length = 80)  private String shipCity;
    @Column(name = "ship_state",  nullable = false, length = 80)  private String shipState;
    @Column(name = "ship_zip",    nullable = false, length = 20)  private String shipZip;
    @Column(name = "ship_country",nullable = false, length = 4)   private String shipCountry;

    @Column(name = "placed_at",  insertable = false, updatable = false) private Instant placedAt;
    @Column(name = "updated_at", insertable = false, updatable = false) private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }        public void setOrderNumber(String v) { this.orderNumber = v; }
    public Long getUserId() { return userId; }                    public void setUserId(Long v) { this.userId = v; }
    public String getStatus() { return status; }                  public void setStatus(String v) { this.status = v; }
    public BigDecimal getSubtotal() { return subtotal; }          public void setSubtotal(BigDecimal v) { this.subtotal = v; }
    public BigDecimal getDiscount() { return discount; }          public void setDiscount(BigDecimal v) { this.discount = v; }
    public BigDecimal getShipping() { return shipping; }          public void setShipping(BigDecimal v) { this.shipping = v; }
    public BigDecimal getTax() { return tax; }                    public void setTax(BigDecimal v) { this.tax = v; }
    public BigDecimal getTotal() { return total; }                public void setTotal(BigDecimal v) { this.total = v; }
    public String getCouponCode() { return couponCode; }          public void setCouponCode(String v) { this.couponCode = v; }
    public String getPaymentMethod() { return paymentMethod; }    public void setPaymentMethod(String v) { this.paymentMethod = v; }
    public String getShipName() { return shipName; }              public void setShipName(String v) { this.shipName = v; }
    public String getShipEmail() { return shipEmail; }            public void setShipEmail(String v) { this.shipEmail = v; }
    public String getShipPhone() { return shipPhone; }            public void setShipPhone(String v) { this.shipPhone = v; }
    public String getShipAddress() { return shipAddress; }        public void setShipAddress(String v) { this.shipAddress = v; }
    public String getShipCity() { return shipCity; }              public void setShipCity(String v) { this.shipCity = v; }
    public String getShipState() { return shipState; }            public void setShipState(String v) { this.shipState = v; }
    public String getShipZip() { return shipZip; }                public void setShipZip(String v) { this.shipZip = v; }
    public String getShipCountry() { return shipCountry; }        public void setShipCountry(String v) { this.shipCountry = v; }
    public Instant getPlacedAt() { return placedAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<OrderItem> getItems() { return items; }
}
