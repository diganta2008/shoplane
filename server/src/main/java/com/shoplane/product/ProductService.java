package com.shoplane.product;

import com.shoplane.common.ApiException;
import com.shoplane.common.PageMeta;
import com.shoplane.product.dto.CategoryDto;
import com.shoplane.product.dto.ProductDto;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository products;
    private final CategoryRepository categories;

    public ProductService(ProductRepository products, CategoryRepository categories) {
        this.products = products;
        this.categories = categories;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> listCategories() {
        return categories.findAllByOrderBySortOrderAscNameAsc()
                .stream().map(CategoryDto::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductDto get(Long id) {
        return products.findByIdAndActiveTrue(id)
                .map(ProductDto::from)
                .orElseThrow(() -> ApiException.notFound("Product not found"));
    }

    @Transactional(readOnly = true)
    public PageResult<ProductDto> search(String categorySlug, Integer categoryId,
                                         BigDecimal minPrice, BigDecimal maxPrice,
                                         BigDecimal minRating, Boolean inStock,
                                         String search, String sort,
                                         int limit, int offset) {

        Specification<Product> spec = Specification.allOf(
                ProductSpecs.active(),
                ProductSpecs.categorySlug(categorySlug),
                ProductSpecs.categoryId(categoryId),
                ProductSpecs.priceBetween(minPrice, maxPrice),
                ProductSpecs.minRating(minRating),
                ProductSpecs.inStock(inStock),
                ProductSpecs.search(search));

        int size = Math.max(1, Math.min(limit, 100));
        int page = Math.max(0, offset / size);
        Pageable pageable = PageRequest.of(page, size, sortSpec(sort));

        Page<Product> pageR = products.findAll(spec, pageable);
        return new PageResult<>(
                pageR.getContent().stream().map(ProductDto::from).toList(),
                new PageMeta(pageR.getTotalElements(), size, offset));
    }

    private static Sort sortSpec(String key) {
        return switch (key == null ? "" : key) {
            case "price_asc"   -> Sort.by(Sort.Order.asc("price"));
            case "price_desc"  -> Sort.by(Sort.Order.desc("price"));
            case "rating_desc" -> Sort.by(Sort.Order.desc("rating"));
            case "newest"      -> Sort.by(Sort.Order.desc("createdAt"));
            case "popular"     -> Sort.by(Sort.Order.desc("reviewCount"));
            default            -> Sort.by(Sort.Order.asc("id"));
        };
    }

    public record PageResult<T>(List<T> items, PageMeta meta) {}
}
