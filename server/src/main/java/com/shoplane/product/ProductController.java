package com.shoplane.product;

import com.shoplane.common.ApiResponse;
import com.shoplane.product.dto.CategoryDto;
import com.shoplane.product.dto.ProductDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Tag(name = "Catalog", description = "Public product & category browsing")
public class ProductController {

    private final ProductService svc;

    public ProductController(ProductService svc) { this.svc = svc; }

    @GetMapping("/api/v1/products")
    public ApiResponse<List<ProductDto>> list(
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "24") int limit,
            @RequestParam(defaultValue = "0")  int offset) {

        var page = svc.search(categorySlug, categoryId, minPrice, maxPrice, minRating,
                inStock, search, sort, limit, offset);
        return ApiResponse.of(page.items(), page.meta());
    }

    @GetMapping("/api/v1/products/{id}")
    public ApiResponse<ProductDto> get(@PathVariable Long id) {
        return ApiResponse.of(svc.get(id));
    }

    @GetMapping({"/api/v1/products/categories", "/api/v1/categories"})
    public ApiResponse<List<CategoryDto>> categories() {
        return ApiResponse.of(svc.listCategories());
    }
}
