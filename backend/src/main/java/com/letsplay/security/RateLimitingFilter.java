package com.letsplay.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letsplay.dto.ApiResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@SuppressWarnings("deprecation")
public class RateLimitingFilter extends OncePerRequestFilter {

    // Allow 100 requests per minute per IP
    private final Map<String, Bucket> cache = new HashMap<>();
    private final Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String key = getClientKey(request);
            Bucket bucket = resolveBucket(key);

            if (bucket.tryConsume(1)) {
                filterChain.doFilter(request, response);
            } else {
                sendRateLimitExceededResponse(response);
            }
        } catch (Exception e) {
            logger.error("Rate limiting filter error: " + e.getMessage());
            filterChain.doFilter(request, response);
        }
    }

    private Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> Bucket4j.builder()
                .addLimit(limit)
                .build());
    }

    private String getClientKey(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    private void sendRateLimitExceededResponse(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(429); // HTTP 429 Too Many Requests

        final ApiResponse<Object> body = new ApiResponse<>();
        body.setStatus("error");
        body.setMessage("Rate limit exceeded. Maximum 100 requests per minute allowed.");

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
