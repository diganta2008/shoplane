package com.shoplane.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Adds an X-Request-Id to every request/response and logs a one-line
 * access record per call. Honours an X-Request-Id supplied by an upstream
 * caller so log correlation flows end-to-end.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);
    public static final String HEADER = "X-Request-Id";
    public static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String id = req.getHeader(HEADER);
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        res.setHeader(HEADER, id);
        req.setAttribute(HEADER, id);
        MDC.put(MDC_KEY, id);
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            long ms = System.currentTimeMillis() - start;
            String origin = req.getHeader("Origin");
            String uri    = req.getRequestURI();
            String qs     = req.getQueryString();
            if (!uri.startsWith("/actuator") && !uri.equals("/error")) {
                log.info("{} {} {} \u2192 {} ({}ms{})",
                        req.getMethod(),
                        uri + (qs != null ? "?" + qs : ""),
                        origin != null ? "origin=" + origin : "no-origin",
                        res.getStatus(),
                        ms,
                        req.getUserPrincipal() != null ? ", user=" + req.getUserPrincipal().getName() : "");
            }
            MDC.remove(MDC_KEY);
        }
    }
}
