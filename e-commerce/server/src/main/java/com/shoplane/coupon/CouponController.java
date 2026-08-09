package com.shoplane.coupon;

import com.shoplane.common.ApiResponse;
import com.shoplane.coupon.dto.ValidateCouponRequest;
import com.shoplane.coupon.dto.ValidateCouponResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coupons")
@Tag(name = "Coupons")
public class CouponController {

    private final CouponService svc;

    public CouponController(CouponService svc) { this.svc = svc; }

    @PostMapping("/validate")
    public ApiResponse<ValidateCouponResponse> validate(@Valid @RequestBody ValidateCouponRequest req) {
        return ApiResponse.of(svc.validate(req.code(), req.subtotal()));
    }
}
