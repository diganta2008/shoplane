package com.shoplane.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddItemRequest(
        @NotNull @Min(1) Long productId,
        @Min(1) @Max(99) Integer qty,
        @Size(max = 32) String size,
        @Size(max = 32) String color) {

    public int quantity() { return qty == null ? 1 : qty; }
}
