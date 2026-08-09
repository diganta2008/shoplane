package com.shoplane.chat;

import com.shoplane.chat.dto.ChatMessage;
import com.shoplane.chat.dto.ChatRequest;
import com.shoplane.chat.dto.ChatResponse;
import com.shoplane.common.ApiException;
import com.shoplane.config.ShopLaneProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles chat requests. When {@code OPENAI_API_KEY} is set the request is
 * forwarded to {@link OpenAiClient}; when blank it falls back to
 * {@link MockChatResponder} which returns canned/random demo replies.
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    /** Cap on history turns retained across a session (each turn ≈ 1 user + 1 assistant msg). */
    private static final int MAX_HISTORY_TURNS = 10;

    private final OpenAiClient client;
    private final MockChatResponder mock;
    private final ShopLaneProperties.OpenAi cfg;

    public ChatService(OpenAiClient client, MockChatResponder mock, ShopLaneProperties props) {
        this.client = client;
        this.mock = mock;
        this.cfg = props.openai();
        if (!hasApiKey()) {
            log.info("Chat: OPENAI_API_KEY is blank — /api/v1/chat will serve canned demo replies.");
        }
    }

    public ChatResponse chat(ChatRequest req) {
        if (!hasApiKey()) {
            return mock.respond(req.message());
        }

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", systemPrompt()));

        List<ChatMessage> history = req.history();
        if (history != null && !history.isEmpty()) {
            int keep = Math.min(history.size(), MAX_HISTORY_TURNS * 2);
            for (ChatMessage m : history.subList(history.size() - keep, history.size())) {
                if ("user".equals(m.role()) || "assistant".equals(m.role())) {
                    messages.add(m);
                }
            }
        }
        messages.add(new ChatMessage("user", req.message()));

        OpenAiClient.Completion c;
        try {
            c = client.complete(messages);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT_ERROR",
                    "Failed to generate chat reply.");
        }
        return new ChatResponse(c.reply(), c.model(), c.usage());
    }

    private boolean hasApiKey() {
        return cfg.apiKey() != null && !cfg.apiKey().isBlank();
    }

    private String systemPrompt() {
        String prompt = cfg.systemPrompt();
        return prompt == null ? "You are a helpful assistant." : prompt;
    }
}
