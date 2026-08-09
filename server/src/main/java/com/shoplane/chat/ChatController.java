package com.shoplane.chat;

import com.shoplane.chat.dto.ChatRequest;
import com.shoplane.chat.dto.ChatResponse;
import com.shoplane.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoint that proxies chat completions to the configured OpenAI-compatible
 * backend. Public (no JWT required) so unauthenticated demo visitors can use the
 * chat widget — protect this in a production deploy (or set OPENAI_API_KEY only on
 * environments where you accept the cost exposure).
 */
@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat")
public class ChatController {

    private final ChatService svc;
    private final MockChatResponder mock;

    public ChatController(ChatService svc, MockChatResponder mock) {
        this.svc = svc;
        this.mock = mock;
    }

    @PostMapping
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest req) {
        return ApiResponse.of(svc.chat(req));
    }

    /** Starter chips the widget shows before the first message. */
    @GetMapping("/suggestions")
    public ApiResponse<List<String>> suggestions() {
        return ApiResponse.of(mock.starterSuggestions());
    }
}
