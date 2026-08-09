package com.shoplane.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveAddressRequest(
        @Size(max = 40)   String label,
        @NotBlank @Size(min = 2, max = 120) String fullName,
        @NotBlank @Size(min = 6, max = 32)  String phone,
        @NotBlank @Size(min = 3, max = 255) String street,
        @NotBlank @Size(min = 2, max = 80)  String city,
        @NotBlank @Size(min = 2, max = 80)  String state,
        @NotBlank @Size(min = 3, max = 20)  String zip,
        @NotBlank @Size(min = 2, max = 4)   String country,
        Boolean isDefault) {}
