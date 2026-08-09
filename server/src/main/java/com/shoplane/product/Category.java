package com.shoplane.product;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 64) private String slug;
    @Column(nullable = false, length = 120)               private String name;
    @Column(length = 255)                                 private String description;

    @Column(name = "sort_order") private Integer sortOrder;

    public Integer getId() { return id; }
    public String getSlug() { return slug; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getSortOrder() { return sortOrder; }
}
