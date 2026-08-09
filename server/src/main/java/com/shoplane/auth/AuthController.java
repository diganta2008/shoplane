package com.shoplane.auth;

import com.shoplane.auth.dto.AuthResponse;
import com.shoplane.auth.dto.LoginRequest;
import com.shoplane.auth.dto.RefreshRequest;
import com.shoplane.auth.dto.RegisterRequest;
import com.shoplane.auth.dto.TokenResponse;
import com.shoplane.common.ApiResponse;
import com.shoplane.user.dto.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Registration, login, refresh, self-lookup, logout")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ApiResponse.of(auth.register(req));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        return ApiResponse.of(auth.login(req, http.getRemoteAddr(), http.getHeader("User-Agent")));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        return ApiResponse.of(auth.refresh(req.refreshToken()));
    }

    @GetMapping("/me")
    public ApiResponse<UserDto> me(@AuthenticationPrincipal AuthenticatedUser me) {
        return ApiResponse.of(auth.me(me.id()));
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, Boolean>> logout() {
        // JWT is stateless; clients drop tokens. Provided for symmetry.
        return ApiResponse.of(Map.of("ok", true));
    }
}
