package com.shoplane.coupon.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ValidateCouponRequest(
        @NotBlank @Size(min = 2, max = 32) String code,
        @NotNull @DecimalMin(value = "0.01") BigDecimal subtotal) {}
