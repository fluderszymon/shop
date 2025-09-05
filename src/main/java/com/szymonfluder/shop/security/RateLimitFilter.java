package com.szymonfluder.shop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Autowired
    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            if (!checkRateLimit(request, response)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Rate limit exceeded. Please try again later.");
                return;
            }
        } catch (Exception e) {
            logger.error("Error processing rate limit", e);
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkRateLimit(HttpServletRequest request, HttpServletResponse response) {
        String clientKey = getClientKey(request);
        boolean allowed = rateLimitService.tryConsume(clientKey, 1);

        if (allowed) {
            long availableTokens = rateLimitService.getAvailableTokens(clientKey);
            response.setHeader("X-RateLimit-Remaining", String.valueOf(availableTokens));
            response.setHeader("X-RateLimit-Limit", String.valueOf(60));
        }
        return allowed;
    }

    private String getClientKey(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        return clientIp + ":" + (userAgent != null ? userAgent.hashCode() : "unknown");
    }
}
