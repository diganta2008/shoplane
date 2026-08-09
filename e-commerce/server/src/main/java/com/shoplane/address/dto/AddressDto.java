package com.shoplane.address.dto;

import com.shoplane.address.Address;

import java.time.Instant;

public record AddressDto(
        Long id,
        String label,
        String fullName,
        String phone,
        String street,
        String city,
        String state,
        String zip,
        String country,
        boolean isDefault,
        Instant createdAt,
        Instant updatedAt) {

    public static AddressDto from(Address a) {
        return new AddressDto(
                a.getId(),
                a.getLabel(),
                a.getFullName(),
                a.getPhone(),
                a.getStreet(),
                a.getCity(),
                a.getState(),
                a.getZip(),
                a.getCountry(),
                a.isDefault(),
                a.getCreatedAt(),
                a.getUpdatedAt());
    }
}
