package com.shoplane.common;

import com.shoplane.user.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health")
public class HealthController {

    private final UserRepository users;
    private final long start = System.currentTimeMillis();

    public HealthController(UserRepository users) { this.users = users; }

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        boolean db;
        try {
            users.count();
            db = true;
        } catch (Exception e) {
            db = false;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", db ? "ok" : "degraded");
        data.put("db", db);
        data.put("uptime", (System.currentTimeMillis() - start) / 1000.0);
        data.put("time", Instant.now().toString());
        return ApiResponse.of(data);
    }
}
