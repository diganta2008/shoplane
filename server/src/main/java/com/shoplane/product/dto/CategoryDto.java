package com.shoplane.product.dto;

import com.shoplane.product.Category;

public record CategoryDto(Integer id, String slug, String name, String description, Integer sortOrder) {
    public static CategoryDto from(Category c) {
        return new CategoryDto(c.getId(), c.getSlug(), c.getName(), c.getDescription(), c.getSortOrder());
    }
}
