package com.shoplane.order;

import com.shoplane.auth.AuthenticatedUser;
import com.shoplane.common.ApiResponse;
import com.shoplane.order.dto.CreateOrderRequest;
import com.shoplane.order.dto.OrderDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders")
public class OrderController {

    private final OrderService svc;

    public OrderController(OrderService svc) { this.svc = svc; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderDto> create(@AuthenticationPrincipal AuthenticatedUser me,
                                        @Valid @RequestBody CreateOrderRequest req) {
        return ApiResponse.of(svc.create(me.id(), req));
    }

    @GetMapping
    public ApiResponse<?> list(@AuthenticationPrincipal AuthenticatedUser me,
                               @RequestParam(defaultValue = "20") int limit,
                               @RequestParam(defaultValue = "0")  int offset) {
        var page = svc.list(me.id(), limit, offset);
        return ApiResponse.of(page.items(), page.meta());
    }

    @GetMapping("/{orderNumber}")
    public ApiResponse<OrderDto> getByNumber(@AuthenticationPrincipal AuthenticatedUser me,
                                             @PathVariable String orderNumber) {
        return ApiResponse.of(svc.getByNumber(me.id(), orderNumber));
    }
}
