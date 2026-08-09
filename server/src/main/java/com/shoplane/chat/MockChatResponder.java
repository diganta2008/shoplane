package com.shoplane.chat;

import com.shoplane.chat.dto.ChatResponse;
import com.shoplane.chat.dto.ChatResponse.TokenUsage;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * Fallback used when no OPENAI_API_KEY is configured. Picks a canned reply
 * based on simple keyword matching, then a random one from a generic pool if
 * nothing matches. Meant purely as a demo mode — quality is intentionally
 * limited so it's obvious this is not real AI.
 *
 * Ordering matters: specific product keywords ("headphone", "laptop") are
 * checked BEFORE the broad "product/item/catalog" bucket so we don't collapse
 * everything into the generic answer.
 */
@Component
public class MockChatResponder {

    private static final Map<Pattern, List<String>> KEYWORD_REPLIES = new LinkedHashMap<>();

    static {
        add("hi|hello|hey|greetings|good (morning|afternoon|evening)",
                "Hey there! Welcome to ShopLane. What can I help you find today?",
                "Hi! Looking for anything specific?",
                "Hello! Ask me about products, shipping, returns, or your account.");

        add("who are you|what are you|are you (ai|real|a bot)|your name",
                "I'm ShopLane Assistant — a demo chatbot running in mock mode. Real LLM answers arrive once the site owner adds an OPENAI_API_KEY.",
                "Just a placeholder bot for now. I match keywords and pick canned replies. Wire up an OpenAI key on the server to get real AI answers.",
                "Mock mode: no LLM behind me right now, just a small script picking pre-written responses.");

        // --- specific product families (must come BEFORE the broad "product" bucket) ---
        add("headphone|earbud|audio|speaker|sound",
                "Popular audio picks include the Aurora Pro over-ears and the Nova wireless earbuds.",
                "Our top-rated headphones (4.8★) have active noise-cancellation and 30-hour battery life.",
                "For gym use I'd suggest earbuds; for travel, over-ear noise-cancelling. Both are on the Shop page.");

        add("watch|smartwatch|wearable|fitness band",
                "The Nova smartwatch is our best-seller — 7-day battery and pairs with both iOS and Android.",
                "Fitness-focused? The Nova Sport tracks heart rate, SpO2, and sleep. See the Wearables filter.",
                "Watches sit in Electronics → Wearables. Prices start around $99.");

        add("laptop|computer|macbook|desktop|pc",
                "We carry a small curated laptop selection — check the Electronics category.",
                "Looking for lightweight or performance? Our top pick pairs a 14\" OLED with all-day battery.",
                "Both Windows and macOS options are in stock. Filter by RAM and battery life on the Shop page.");

        add("phone|smartphone|iphone|android|mobile",
                "Latest flagships from both iOS and Android are in Electronics → Mobiles.",
                "For budget picks under $500, sort the Mobiles category by price ascending.",
                "Every phone ships unlocked and dual-SIM ready.");

        add("book|read|novel|paperback|hardcover|kindle",
                "Books span fiction, non-fiction, and design references — see the Books category.",
                "Popular this week: 'The Design Companion' and 'Everyday Neuroscience'.",
                "All books ship with free tracked delivery over $25.");

        add("shirt|jeans|jacket|dress|sweater|clothing|fashion|apparel|wear",
                "Fashion covers casual, work, and outerwear — filter by size in the Fashion category.",
                "Returns on clothing are free for 30 days if the tags are still attached.",
                "Our sizing runs a hair small — consider going one size up for slim-fit items.");

        add("coffee|blender|kitchen|home appliance|vacuum|lamp",
                "Home goods and small appliances live in the Home category. Espresso and blenders are top-sellers.",
                "The Barista Pro machine is a customer favourite — 4.9★ with a permanent 10% loyalty discount.",
                "Vacuums, lamps, and cookware are all in Home → Living. Free delivery over $100.");

        // --- policy / info topics ---
        add("shipping|delivery|deliver|arrive|when will|how long",
                "Standard delivery is 2–3 business days. Free on orders over $100.",
                "We ship in 2–3 business days across the country. Free shipping kicks in over $100.",
                "Orders usually arrive in 2–3 days. Expedited (next-day) is available at checkout.");

        add("return|refund|exchange|money back",
                "You can return anything within 30 days for a full refund — no questions asked.",
                "Returns are free for 30 days from delivery. Print the pre-paid label from your order page.",
                "We do 30-day free returns and exchanges. Head to your order in the Account menu to start one.");

        add("price|cost|expensive|cheap|discount|coupon|sale|promo|deal",
                "Prices are on each product page. Try coupon code SAVE20 for 20% off, or WELCOME10 for first-time buyers.",
                "We have three active codes: WELCOME10, SAVE20, FREESHIP. Apply at checkout.",
                "For the best deals, check the Summer Clearance section — up to 50% off.");

        add("cart|checkout|buy|purchase|order",
                "Your cart lives in the top-right icon. Checkout is one page — address, payment, done.",
                "You can review your cart, apply a coupon, and complete checkout from the cart icon.",
                "After placing an order you'll get an email confirmation and it'll show under Account → Orders.");

        add("account|login|log in|sign in|register|sign up|password",
                "Register or log in from the top-right avatar. Your cart and wishlist sync to your account.",
                "Forgot password? Use the reset link on the login page (email delivery may take a minute).",
                "You can update your name, email, and password anytime in Account → Profile.");

        add("wishlist|favorite|favourite|save for later|heart",
                "Wishlist is on every product page — hit the heart icon. See saved items under Account → Wishlist.",
                "Wishlists survive across devices when you're logged in.",
                "Star anything you like and I'll keep it in your wishlist until you're ready to buy.");

        add("support|help|contact|customer service|email|phone number",
                "Reach us at support@shoplane.com or through the Contact page — average reply time is under 2 hours.",
                "Live chat (that's me!) and email are the fastest. Phone lines are open Mon–Fri, 9am–6pm.",
                "The Contact page has a form for pre-sale, returns, and account queries.");

        add("thank|thanks|thx|thnx|thankyou|ty",
                "Anytime! Ping me if you need anything else.",
                "You're welcome. Happy shopping!",
                "Glad I could help. Have a good one.");

        add("bye|goodbye|see you|later|cya",
                "Take care! I'm here whenever you need me.",
                "Bye — enjoy your shopping.",
                "See you later. The bubble stays open on every page.");

        // --- broad fallback bucket, checked LAST ---
        add("product|item|catalog|what.*sell|categor|browse|inventory|stock",
                "We stock electronics, fashion, home goods, and books. Browse the Shop page for the full catalog.",
                "Top categories right now: audio gear, watches, home appliances, and coffee equipment.",
                "Have a look at the Shop page — 100+ items across four categories, filterable by price and rating.");
    }

    /** Adds a keyword pattern with its list of possible replies. Order-preserving. */
    private static void add(String pattern, String... replies) {
        // Leading \b prevents "chair" matching "ir"; no trailing boundary so
        // plurals ("headphones") and prefixes still match.
        KEYWORD_REPLIES.put(
                Pattern.compile("(?i).*\\b(" + pattern + ").*"),
                List.of(replies));
    }

    /** Generic pool used when nothing matches. */
    private static final List<String> FALLBACK_REPLIES = List.of(
            "That's a great question! I'm running in demo mode right now, so my answers are pre-written — but I can still help with shipping, returns, coupons, and products.",
            "Interesting — for the full answer you'd need real AI enabled on this site. Meanwhile, try asking about products, shipping, or returns.",
            "I'm not sure I understood that. Try asking me about our products, shipping, returns, or coupons.",
            "Hmm, I don't have a canned answer for that. In demo mode I know about shipping (2–3 days, free over $100), returns (30 days), and coupons (SAVE20, WELCOME10).",
            "I'd love to help with that — but this demo bot only handles a small set of topics. Try 'shipping', 'returns', 'coupons', or 'products'.",
            "Good one! For that I'd need real AI hooked up. For now I can chat about products, orders, cart, wishlist, or delivery."
    );

    /** Generates a canned reply and rough fake token stats. */
    public ChatResponse respond(String userMessage) {
        String reply = pickReply(userMessage == null ? "" : userMessage.toLowerCase(Locale.ROOT));
        int approxPrompt = (userMessage == null ? 0 : userMessage.length()) / 4;
        int approxOut    = reply.length() / 4;
        return new ChatResponse(reply, "mock-shoplane-v1",
                new TokenUsage(approxPrompt, approxOut, approxPrompt + approxOut));
    }

    private String pickReply(String lower) {
        for (Map.Entry<Pattern, List<String>> e : KEYWORD_REPLIES.entrySet()) {
            if (e.getKey().matcher(lower).matches()) {
                return random(e.getValue());
            }
        }
        return random(FALLBACK_REPLIES);
    }

    private static String random(List<String> options) {
        return options.get(ThreadLocalRandom.current().nextInt(options.size()));
    }
}
