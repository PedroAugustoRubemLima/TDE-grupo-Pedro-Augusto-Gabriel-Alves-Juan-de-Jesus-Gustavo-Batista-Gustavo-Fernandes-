package com.seuprojeto.lojadesktop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final long WINDOW_MILLIS = 60_000;
    private static final int API_LIMIT_PER_MINUTE = 120;
    private static final int LOGIN_LIMIT_PER_MINUTE = 15;

    private final Map<String, Deque<Long>> requestTimestamps = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        int limit = path.startsWith("/api/auth/login")
                ? LOGIN_LIMIT_PER_MINUTE
                : API_LIMIT_PER_MINUTE;

        String key = getClientIp(request) + "|" + request.getMethod() + "|" + getBucket(path);
        if (!allowRequest(key, limit)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\":\"Muitas requisições. Tente novamente em instantes.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getBucket(String path) {
        if (path.startsWith("/api/auth/login")) {
            return "AUTH_LOGIN";
        }
        return "API";
    }

    private boolean allowRequest(String key, int limit) {
        long now = Instant.now().toEpochMilli();
        Deque<Long> deque = requestTimestamps.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());
        long threshold = now - WINDOW_MILLIS;

        while (!deque.isEmpty() && deque.peekFirst() < threshold) {
            deque.pollFirst();
        }

        if (deque.size() >= limit) {
            return false;
        }

        deque.addLast(now);
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
