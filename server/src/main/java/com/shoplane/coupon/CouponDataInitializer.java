package com.shoplane.coupon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds the Independence-Day promo coupons at startup. Idempotent — safe to
 * re-run on every boot. Kept in-code (not just SQL) so the codes work even on
 * databases seeded before these coupons existed (e.g. Railway MySQL, or a
 * local Docker MySQL that was initialised before the promo was added).
 *
 * If you want to change the promo window, edit the constants below.
 */
@Component
@Order(1)
public class CouponDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CouponDataInitializer.class);

    private static final LocalDate PROMO_START = LocalDate.of(2026, 8, 1);
    private static final LocalDate PROMO_END   = LocalDate.of(2026, 8, 20);

    private final JdbcTemplate jdbc;

    public CouponDataInitializer(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override
    public void run(String... args) {
        upsert("AZADI15", "15% off (Independence Day)", "percent",
                new BigDecimal("15.00"), BigDecimal.ZERO);
        upsert("AZADI25", "25% off on orders over $200 (Independence Day)", "percent",
                new BigDecimal("25.00"), new BigDecimal("200.00"));
    }

    private void upsert(String code, String description, String type,
                        BigDecimal value, BigDecimal minSubtotal) {
        // MySQL upsert — replaces the row if the primary key (code) already exists.
        // We keep it_active=1 and refresh the validity window on every boot so
        // an operator can extend the promo just by bumping PROMO_END and
        // restarting the service.
        int updated = jdbc.update(
                """
                INSERT INTO coupons (code, description, discount_type, value,
                                     min_subtotal, is_active, valid_from, valid_until)
                VALUES (?, ?, ?, ?, ?, 1, ?, ?)
                ON DUPLICATE KEY UPDATE
                    description  = VALUES(description),
                    discount_type= VALUES(discount_type),
                    value        = VALUES(value),
                    min_subtotal = VALUES(min_subtotal),
                    is_active    = 1,
                    valid_from   = VALUES(valid_from),
                    valid_until  = VALUES(valid_until)
                """,
                code, description, type, value, minSubtotal,
                java.sql.Date.valueOf(PROMO_START),
                java.sql.Date.valueOf(PROMO_END));
        log.info("Coupon upsert: code={}, rowsAffected={}", code, updated);
    }
}
