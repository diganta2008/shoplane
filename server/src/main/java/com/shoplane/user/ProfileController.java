package com.shoplane.user;

import com.shoplane.auth.AuthenticatedUser;
import com.shoplane.common.ApiResponse;
import com.shoplane.user.dto.ChangePasswordRequest;
import com.shoplane.user.dto.UpdateProfileRequest;
import com.shoplane.user.dto.UserProfileDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile")
public class ProfileController {

    private final ProfileService svc;

    public ProfileController(ProfileService svc) { this.svc = svc; }

    @GetMapping
    public ApiResponse<UserProfileDto> get(@AuthenticationPrincipal AuthenticatedUser me) {
        return ApiResponse.of(svc.get(me.id()));
    }

    @PatchMapping
    public ApiResponse<UserProfileDto> update(@AuthenticationPrincipal AuthenticatedUser me,
                                              @Valid @RequestBody UpdateProfileRequest req) {
        return ApiResponse.of(svc.update(me.id(), req));
    }

    @PostMapping("/change-password")
    public ApiResponse<Map<String, Boolean>> changePassword(@AuthenticationPrincipal AuthenticatedUser me,
                                                            @Valid @RequestBody ChangePasswordRequest req) {
        svc.changePassword(me.id(), req);
        return ApiResponse.of(Map.of("ok", true));
    }
}
