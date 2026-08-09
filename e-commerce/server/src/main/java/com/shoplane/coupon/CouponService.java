package com.shoplane.coupon;

import com.shoplane.common.ApiException;
import com.shoplane.coupon.dto.ValidateCouponResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class CouponService {

    private static final BigDecimal CAP_PERCENT = new BigDecimal("90");

    private final CouponRepository coupons;

    public CouponService(CouponRepository coupons) { this.coupons = coupons; }

    @Transactional(readOnly = true)
    public ValidateCouponResponse validate(String code, BigDecimal subtotal) {
        Coupon c = coupons.findActive(code, LocalDate.now())
                .orElseThrow(() -> ApiException.notFound("Coupon not found or inactive"));
        if (subtotal.compareTo(c.getMinSubtotal()) < 0) {
            throw ApiException.badRequest(
                    "Minimum subtotal of " + c.getMinSubtotal() + " required for this coupon");
        }
        return new ValidateCouponResponse(
                c.getCode(), c.getDescription(), c.getDiscountType(),
                c.getValue(), c.getMinSubtotal(),
                computeDiscount(c, subtotal));
    }

    /** Public: also used from OrderService when placing an order. */
    public BigDecimal computeDiscount(Coupon c, BigDecimal subtotal) {
        if (subtotal.compareTo(c.getMinSubtotal()) < 0) return BigDecimal.ZERO;
        if ("percent".equalsIgnoreCase(c.getDiscountType())) {
            BigDecimal pct = c.getValue().min(CAP_PERCENT);
            return subtotal.multiply(pct)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        return c.getValue().min(subtotal);
    }

    public Coupon require(String code) {
        return coupons.findActive(code, LocalDate.now())
                .orElseThrow(() -> ApiException.badRequest("Invalid coupon: " + code));
    }
}
