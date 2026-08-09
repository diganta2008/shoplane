package com.shoplane.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateQtyRequest(@NotNull @Min(0) @Max(99) Integer qty) {}
