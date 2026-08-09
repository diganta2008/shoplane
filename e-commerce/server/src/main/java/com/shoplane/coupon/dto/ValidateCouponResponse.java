package com.shoplane.coupon.dto;

import java.math.BigDecimal;

public record ValidateCouponResponse(
        String code,
        String description,
        String discountType,
        BigDecimal value,
        BigDecimal minSubtotal,
        BigDecimal discount) {}
