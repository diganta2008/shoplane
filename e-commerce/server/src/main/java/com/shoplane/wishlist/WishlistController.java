package com.shoplane.wishlist;

import com.shoplane.auth.AuthenticatedUser;
import com.shoplane.common.ApiResponse;
import com.shoplane.wishlist.dto.AddWishlistRequest;
import com.shoplane.wishlist.dto.WishlistEntryDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wishlist")
@Tag(name = "Wishlist")
public class WishlistController {

    private final WishlistService svc;

    public WishlistController(WishlistService svc) { this.svc = svc; }

    @GetMapping
    public ApiResponse<List<WishlistEntryDto>> list(@AuthenticationPrincipal AuthenticatedUser me) {
        return ApiResponse.of(svc.list(me.id()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String, Boolean>> add(@AuthenticationPrincipal AuthenticatedUser me,
                                                 @Valid @RequestBody AddWishlistRequest req) {
        svc.add(me.id(), req.productId());
        return ApiResponse.of(Map.of("ok", true));
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Map<String, Boolean>> remove(@AuthenticationPrincipal AuthenticatedUser me,
                                                    @PathVariable Long productId) {
        svc.remove(me.id(), productId);
        return ApiResponse.of(Map.of("ok", true));
    }
}
