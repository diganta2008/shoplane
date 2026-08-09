package com.shoplane.wishlist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddWishlistRequest(@NotNull @Min(1) Long productId) {}
