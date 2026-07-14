package com.letsplay.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final int maxRequests;
    private final Duration timeWindow;
    private final Map<String, RequestBucket> buckets = new ConcurrentHashMap<>();

    public RateLimitingFilter() {
        this(10, 60);
    }

    public RateLimitingFilter(int maxRequests, int seconds) {
        this.maxRequests = maxRequests;
        this.timeWindow = Duration.ofSeconds(seconds);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientId = request.getRemoteAddr();
        if (clientId == null || clientId.isBlank()) {
            clientId = "unknown";
        }

        RequestBucket bucket = buckets.computeIfAbsent(clientId, key -> new RequestBucket(timeWindow));
        long now = System.currentTimeMillis();

        if (bucket.isExpired(now)) {
            bucket.reset(now);
        }

        if (bucket.incrementAndCheck(now, maxRequests)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":\"error\",\"data\":null,\"message\":\"Too many requests\"}");
    }

    private static class RequestBucket {
        private final Duration timeWindow;
        private int count;
        private long windowStart;

        private RequestBucket(Duration timeWindow) {
            this.timeWindow = timeWindow;
        }

        boolean isExpired(long now) {
            return now - windowStart > timeWindow.toMillis();
        }

        void reset(long now) {
            count = 0;
            windowStart = now;
        }

        boolean incrementAndCheck(long now, int maxRequests) {
            if (count == 0) {
                windowStart = now;
            }
            count++;
            return count <= maxRequests;
        }
    }
}
