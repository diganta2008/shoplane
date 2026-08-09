package com.shoplane.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank @Pattern(regexp = "^(card|upi|cod)$",
                message = "paymentMethod must be one of: card, upi, cod") String paymentMethod,
        @Size(max = 32) String couponCode,
        @DecimalMin("0.00") BigDecimal shippingFee,
        @DecimalMin("0.00") @DecimalMax("0.5") BigDecimal taxRate,
        // Either point at a saved address book entry ...
        Long addressId,
        // ... or supply a one-off shipping block. One of the two must be present.
        @Valid ShippingAddress shipping,
        List<@Valid OrderLine> items) {

    public record ShippingAddress(
            @NotBlank @Size(min = 2, max = 120) String name,
            @NotBlank @Email  @Size(max = 190) String email,
            @NotBlank @Size(min = 6, max = 32)  String phone,
            @NotBlank @Size(min = 3, max = 255) String address,
            @NotBlank @Size(min = 2, max = 80)  String city,
            @NotBlank @Size(min = 2, max = 80)  String state,
            @NotBlank @Size(min = 3, max = 20)  String zip,
            @NotBlank @Size(min = 2, max = 4)   String country) {}

    public record OrderLine(
            @NotNull @Min(1) Long productId,
            @NotNull @Min(1) @Max(99) Integer qty,
            @Size(max = 32) String size,
            @Size(max = 32) String color) {}
}
