package com.shoplane.chat;

import com.shoplane.chat.dto.ChatMessage;
import com.shoplane.chat.dto.ChatRequest;
import com.shoplane.chat.dto.ChatResponse;
import com.shoplane.common.ApiException;
import com.shoplane.config.ShopLaneProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembles the OpenAI chat/completions request from a caller's message and
 * history, then delegates to the low-level {@link OpenAiClient}.
 */
@Service
public class ChatService {

    /** Cap on history turns retained across a session (each turn ≈ 1 user + 1 assistant msg). */
    private static final int MAX_HISTORY_TURNS = 10;

    private final OpenAiClient client;
    private final ShopLaneProperties.OpenAi cfg;

    public ChatService(OpenAiClient client, ShopLaneProperties props) {
        this.client = client;
        this.cfg = props.openai();
    }

    public ChatResponse chat(ChatRequest req) {
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

    private String systemPrompt() {
        String prompt = cfg.systemPrompt();
        return prompt == null ? "You are a helpful assistant." : prompt;
    }
}
