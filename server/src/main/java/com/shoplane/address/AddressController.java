package com.shoplane.address;

import com.shoplane.address.dto.AddressDto;
import com.shoplane.address.dto.SaveAddressRequest;
import com.shoplane.auth.AuthenticatedUser;
import com.shoplane.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/addresses")
@Tag(name = "Addresses")
public class AddressController {

    private final AddressService svc;

    public AddressController(AddressService svc) { this.svc = svc; }

    @GetMapping
    public ApiResponse<List<AddressDto>> list(@AuthenticationPrincipal AuthenticatedUser me) {
        return ApiResponse.of(svc.list(me.id()));
    }

    @GetMapping("/{id}")
    public ApiResponse<AddressDto> get(@AuthenticationPrincipal AuthenticatedUser me,
                                       @PathVariable Long id) {
        return ApiResponse.of(svc.get(me.id(), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AddressDto> create(@AuthenticationPrincipal AuthenticatedUser me,
                                          @Valid @RequestBody SaveAddressRequest req) {
        return ApiResponse.of(svc.create(me.id(), req));
    }

    @PatchMapping("/{id}")
    public ApiResponse<AddressDto> update(@AuthenticationPrincipal AuthenticatedUser me,
                                          @PathVariable Long id,
                                          @Valid @RequestBody SaveAddressRequest req) {
        return ApiResponse.of(svc.update(me.id(), id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Boolean>> delete(@AuthenticationPrincipal AuthenticatedUser me,
                                                    @PathVariable Long id) {
        svc.delete(me.id(), id);
        return ApiResponse.of(Map.of("ok", true));
    }

    @PostMapping("/{id}/default")
    public ApiResponse<AddressDto> makeDefault(@AuthenticationPrincipal AuthenticatedUser me,
                                               @PathVariable Long id) {
        return ApiResponse.of(svc.setDefault(me.id(), id));
    }
}
