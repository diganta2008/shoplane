package com.shoplane.coupon;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @Column(length = 32) private String code;

    @Column(nullable = false, length = 120) private String description;

    @Column(name = "discount_type", nullable = false, length = 10)
    private String discountType;

    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal value;

    @Column(name = "min_subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal minSubtotal;

    @Column(name = "is_active", nullable = false) private Boolean active = true;

    @Column(name = "valid_from")  private LocalDate validFrom;
    @Column(name = "valid_until") private LocalDate validUntil;

    public String getCode() { return code; }
    public String getDescription() { return description; }
    public String getDiscountType() { return discountType; }
    public BigDecimal getValue() { return value; }
    public BigDecimal getMinSubtotal() { return minSubtotal; }
    public Boolean getActive() { return active; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidUntil() { return validUntil; }
}
