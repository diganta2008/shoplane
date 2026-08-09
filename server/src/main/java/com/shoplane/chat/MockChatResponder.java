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
 * Each bucket carries three reply variants (chosen at random) AND three
 * topical follow-up suggestions the UI renders as clickable chips.
 *
 * Ordering matters: specific product families (headphones, laptops) are
 * checked BEFORE the broad "product" bucket, and greetings/identity match
 * before everything else.
 */
@Component
public class MockChatResponder {

    /** One keyword bucket: alternate replies + suggested follow-up prompts. */
    private record Bucket(List<String> replies, List<String> suggestions) {}

    private static final Map<Pattern, Bucket> KEYWORD_REPLIES = new LinkedHashMap<>();

    static {
        add("hi|hello|hey|greetings|good (morning|afternoon|evening)",
                new String[]{
                        "Hey there! Welcome to ShopLane. What can I help you find today?",
                        "Hi! Looking for anything specific?",
                        "Hello! Ask me about products, shipping, returns, or your account."
                },
                new String[]{
                        "What do you sell?",
                        "How long does shipping take?",
                        "Any coupon codes?"
                });

        add("who are you|what are you|are you (ai|real|a bot)|your name",
                new String[]{
                        "I'm ShopLane Assistant — a demo chatbot running in mock mode. Real LLM answers arrive once the site owner adds an OPENAI_API_KEY.",
                        "Just a placeholder bot for now. I match keywords and pick canned replies. Wire up an OpenAI key on the server to get real AI answers.",
                        "Mock mode: no LLM behind me right now, just a small script picking pre-written responses."
                },
                new String[]{
                        "What can you help with?",
                        "Show me your best products",
                        "How do I contact support?"
                });

        // --- specific product families (before broad "product") ---
        add("headphone|earbud|audio|speaker|sound",
                new String[]{
                        "Popular audio picks include the Aurora Pro over-ears and the Nova wireless earbuds.",
                        "Our top-rated headphones (4.8★) have active noise-cancellation and 30-hour battery life.",
                        "For gym use I'd suggest earbuds; for travel, over-ear noise-cancelling. Both are on the Shop page."
                },
                new String[]{
                        "Best earbuds under $100?",
                        "Which headphones have noise-cancellation?",
                        "Do you offer a warranty on audio?"
                });

        add("watch|smartwatch|wearable|fitness band",
                new String[]{
                        "The Nova smartwatch is our best-seller — 7-day battery and pairs with both iOS and Android.",
                        "Fitness-focused? The Nova Sport tracks heart rate, SpO2, and sleep. See the Wearables filter.",
                        "Watches sit in Electronics → Wearables. Prices start around $99."
                },
                new String[]{
                        "Compare Nova and Nova Sport",
                        "Best smartwatch for running?",
                        "Show me watches under $150"
                });

        add("laptop|computer|macbook|desktop|pc",
                new String[]{
                        "We carry a small curated laptop selection — check the Electronics category.",
                        "Looking for lightweight or performance? Our top pick pairs a 14\" OLED with all-day battery.",
                        "Both Windows and macOS options are in stock. Filter by RAM and battery life on the Shop page."
                },
                new String[]{
                        "Best laptop for students?",
                        "Any gaming laptops?",
                        "Which laptop has the longest battery?"
                });

        add("phone|smartphone|iphone|android|mobile",
                new String[]{
                        "Latest flagships from both iOS and Android are in Electronics → Mobiles.",
                        "For budget picks under $500, sort the Mobiles category by price ascending.",
                        "Every phone ships unlocked and dual-SIM ready."
                },
                new String[]{
                        "Best camera phone?",
                        "Phones under $500",
                        "Is trade-in available?"
                });

        add("book|read|novel|paperback|hardcover|kindle",
                new String[]{
                        "Books span fiction, non-fiction, and design references — see the Books category.",
                        "Popular this week: 'The Design Companion' and 'Everyday Neuroscience'.",
                        "All books ship with free tracked delivery over $25."
                },
                new String[]{
                        "Any bestsellers this month?",
                        "Do you sell audiobooks?",
                        "Recommend a design book"
                });

        add("shirt|jeans|jacket|dress|sweater|clothing|fashion|apparel|wear",
                new String[]{
                        "Fashion covers casual, work, and outerwear — filter by size in the Fashion category.",
                        "Returns on clothing are free for 30 days if the tags are still attached.",
                        "Our sizing runs a hair small — consider going one size up for slim-fit items."
                },
                new String[]{
                        "Do you have a size guide?",
                        "How do I return an item?",
                        "New arrivals this week"
                });

        add("coffee|blender|kitchen|home appliance|vacuum|lamp",
                new String[]{
                        "Home goods and small appliances live in the Home category. Espresso and blenders are top-sellers.",
                        "The Barista Pro machine is a customer favourite — 4.9★ with a permanent 10% loyalty discount.",
                        "Vacuums, lamps, and cookware are all in Home → Living. Free delivery over $100."
                },
                new String[]{
                        "Best espresso machine?",
                        "Any deals on vacuums?",
                        "How long is the warranty?"
                });

        // --- policy / info topics ---
        add("shipping|delivery|deliver|arrive|when will|how long",
                new String[]{
                        "Standard delivery is 2–3 business days. Free on orders over $100.",
                        "We ship in 2–3 business days across the country. Free shipping kicks in over $100.",
                        "Orders usually arrive in 2–3 days. Expedited (next-day) is available at checkout."
                },
                new String[]{
                        "Do you ship internationally?",
                        "How do I track my order?",
                        "Is next-day delivery available?"
                });

        add("return|refund|exchange|money back",
                new String[]{
                        "You can return anything within 30 days for a full refund — no questions asked.",
                        "Returns are free for 30 days from delivery. Print the pre-paid label from your order page.",
                        "We do 30-day free returns and exchanges. Head to your order in the Account menu to start one."
                },
                new String[]{
                        "How do I start a return?",
                        "When will I get my refund?",
                        "Can I exchange for a different size?"
                });

        add("price|cost|expensive|cheap|discount|coupon|sale|promo|deal",
                new String[]{
                        "Prices are on each product page. Try coupon code SAVE20 for 20% off, or WELCOME10 for first-time buyers.",
                        "We have three active codes: WELCOME10, SAVE20, FREESHIP. Apply at checkout.",
                        "For the best deals, check the Summer Clearance section — up to 50% off."
                },
                new String[]{
                        "Show me the clearance section",
                        "How do I apply a coupon?",
                        "Any student discounts?"
                });

        add("cart|checkout|buy|purchase|order",
                new String[]{
                        "Your cart lives in the top-right icon. Checkout is one page — address, payment, done.",
                        "You can review your cart, apply a coupon, and complete checkout from the cart icon.",
                        "After placing an order you'll get an email confirmation and it'll show under Account → Orders."
                },
                new String[]{
                        "What payment methods do you accept?",
                        "Can I edit my order after placing it?",
                        "How do I track my order?"
                });

        add("account|login|log in|sign in|register|sign up|password",
                new String[]{
                        "Register or log in from the top-right avatar. Your cart and wishlist sync to your account.",
                        "Forgot password? Use the reset link on the login page (email delivery may take a minute).",
                        "You can update your name, email, and password anytime in Account → Profile."
                },
                new String[]{
                        "I forgot my password",
                        "How do I delete my account?",
                        "Do you offer social login?"
                });

        add("wishlist|favorite|favourite|save for later|heart",
                new String[]{
                        "Wishlist is on every product page — hit the heart icon. See saved items under Account → Wishlist.",
                        "Wishlists survive across devices when you're logged in.",
                        "Star anything you like and I'll keep it in your wishlist until you're ready to buy."
                },
                new String[]{
                        "How do I share my wishlist?",
                        "Move wishlist items to cart",
                        "Do wishlist prices update?"
                });

        add("support|help|contact|customer service|email|phone number",
                new String[]{
                        "Reach us at support@shoplane.com or through the Contact page — average reply time is under 2 hours.",
                        "Live chat (that's me!) and email are the fastest. Phone lines are open Mon–Fri, 9am–6pm.",
                        "The Contact page has a form for pre-sale, returns, and account queries."
                },
                new String[]{
                        "What are your support hours?",
                        "How do I report a damaged item?",
                        "I need help with an order"
                });

        add("thank|thanks|thx|thnx|thankyou|ty",
                new String[]{
                        "Anytime! Ping me if you need anything else.",
                        "You're welcome. Happy shopping!",
                        "Glad I could help. Have a good one."
                },
                new String[]{
                        "Show me new arrivals",
                        "Any coupon codes?",
                        "Take me to the shop"
                });

        add("bye|goodbye|see you|later|cya",
                new String[]{
                        "Take care! I'm here whenever you need me.",
                        "Bye — enjoy your shopping.",
                        "See you later. The bubble stays open on every page."
                },
                new String[]{
                        "One more question…",
                        "Show me trending items",
                        "Sign me up for the newsletter"
                });

        // --- broad fallback bucket, checked LAST ---
        add("product|item|catalog|what.*sell|categor|browse|inventory|stock",
                new String[]{
                        "We stock electronics, fashion, home goods, and books. Browse the Shop page for the full catalog.",
                        "Top categories right now: audio gear, watches, home appliances, and coffee equipment.",
                        "Have a look at the Shop page — 100+ items across four categories, filterable by price and rating."
                },
                new String[]{
                        "Show me electronics",
                        "Fashion picks for men",
                        "What's on sale?"
                });
    }

    private static void add(String pattern, String[] replies, String[] suggestions) {
        KEYWORD_REPLIES.put(
                Pattern.compile("(?i).*\\b(" + pattern + ").*"),
                new Bucket(List.of(replies), List.of(suggestions)));
    }

    /** Generic replies used when nothing matches. */
    private static final List<String> FALLBACK_REPLIES = List.of(
            "That's a great question! I'm running in demo mode right now, so my answers are pre-written — but I can still help with shipping, returns, coupons, and products.",
            "Interesting — for the full answer you'd need real AI enabled on this site. Meanwhile, try asking about products, shipping, or returns.",
            "I'm not sure I understood that. Try asking me about our products, shipping, returns, or coupons.",
            "Hmm, I don't have a canned answer for that. In demo mode I know about shipping (2–3 days, free over $100), returns (30 days), and coupons (SAVE20, WELCOME10).",
            "I'd love to help with that — but this demo bot only handles a small set of topics. Try 'shipping', 'returns', 'coupons', or 'products'.",
            "Good one! For that I'd need real AI hooked up. For now I can chat about products, orders, cart, wishlist, or delivery."
    );

    /** Generic starter suggestions surfaced with the greeting and after fallbacks. */
    private static final List<String> STARTER_SUGGESTIONS = List.of(
            "How long does shipping take?",
            "What's your return policy?",
            "Any coupon codes?",
            "Show me best-selling headphones",
            "Recommend a laptop",
            "How do I track my order?"
    );

    /** Generates a canned reply, topical suggestions, and rough fake token stats. */
    public ChatResponse respond(String userMessage) {
        String lower = userMessage == null ? "" : userMessage.toLowerCase(Locale.ROOT);

        String reply;
        List<String> suggestions;
        Bucket match = findBucket(lower);
        if (match != null) {
            reply = random(match.replies());
            suggestions = match.suggestions();
        } else {
            reply = random(FALLBACK_REPLIES);
            suggestions = STARTER_SUGGESTIONS;
        }

        int approxPrompt = (userMessage == null ? 0 : userMessage.length()) / 4;
        int approxOut    = reply.length() / 4;
        return new ChatResponse(reply, "mock-shoplane-v1",
                new TokenUsage(approxPrompt, approxOut, approxPrompt + approxOut),
                suggestions);
    }

    /** Public so the ChatController can expose starter chips before the first message. */
    public List<String> starterSuggestions() {
        return STARTER_SUGGESTIONS;
    }

    private Bucket findBucket(String lower) {
        for (Map.Entry<Pattern, Bucket> e : KEYWORD_REPLIES.entrySet()) {
            if (e.getKey().matcher(lower).matches()) {
                return e.getValue();
            }
        }
        return null;
    }

    private static String random(List<String> options) {
        return options.get(ThreadLocalRandom.current().nextInt(options.size()));
    }
}
