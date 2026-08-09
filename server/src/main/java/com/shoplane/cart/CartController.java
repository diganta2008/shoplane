package com.shoplane.cart;

import com.shoplane.auth.AuthenticatedUser;
import com.shoplane.cart.dto.AddItemRequest;
import com.shoplane.cart.dto.CartDto;
import com.shoplane.cart.dto.UpdateQtyRequest;
import com.shoplane.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart")
public class CartController {

    private final CartService svc;

    public CartController(CartService svc) { this.svc = svc; }

    @GetMapping
    public ApiResponse<CartDto> get(@AuthenticationPrincipal AuthenticatedUser me) {
        return ApiResponse.of(svc.get(me.id()));
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CartDto> add(@AuthenticationPrincipal AuthenticatedUser me,
                                    @Valid @RequestBody AddItemRequest req) {
        return ApiResponse.of(svc.add(me.id(), req));
    }

    @PatchMapping("/items/{itemId}")
    public ApiResponse<CartDto> update(@AuthenticationPrincipal AuthenticatedUser me,
                                       @PathVariable Long itemId,
                                       @Valid @RequestBody UpdateQtyRequest req) {
        return ApiResponse.of(svc.updateQty(me.id(), itemId, req.qty()));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartDto> remove(@AuthenticationPrincipal AuthenticatedUser me,
                                       @PathVariable Long itemId) {
        return ApiResponse.of(svc.remove(me.id(), itemId));
    }

    @DeleteMapping
    public ApiResponse<CartDto> clear(@AuthenticationPrincipal AuthenticatedUser me) {
        return ApiResponse.of(svc.clear(me.id()));
    }
}
