package com.shoplane.product;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic filter builder for GET /products. Anything the controller
 * receives as a query-param can be layered on with `and`.
 */
public class ProductSpecs {

    private ProductSpecs() {}

    public static Specification<Product> active() {
        return (root, q, cb) -> cb.isTrue(root.get("active"));
    }

    public static Specification<Product> categorySlug(String slug) {
        if (slug == null || slug.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("category").get("slug"), slug);
    }

    public static Specification<Product> categoryId(Integer id) {
        if (id == null) return null;
        return (root, q, cb) -> cb.equal(root.get("category").get("id"), id);
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        if (min == null && max == null) return null;
        return (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (min != null) ps.add(cb.greaterThanOrEqualTo(root.get("price"), min));
            if (max != null) ps.add(cb.lessThanOrEqualTo(root.get("price"), max));
            return cb.and(ps.toArray(Predicate[]::new));
        };
    }

    public static Specification<Product> minRating(BigDecimal r) {
        if (r == null) return null;
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("rating"), r);
    }

    public static Specification<Product> inStock(Boolean flag) {
        if (flag == null || !flag) return null;
        return (root, q, cb) -> cb.greaterThan(root.get("stock"), 0);
    }

    public static Specification<Product> search(String q) {
        if (q == null || q.isBlank()) return null;
        String pat = "%" + q.toLowerCase() + "%";
        return (root, cq, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pat),
                cb.like(cb.lower(root.get("description")), pat));
    }
}
