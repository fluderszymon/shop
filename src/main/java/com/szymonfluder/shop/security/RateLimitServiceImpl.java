package com.szymonfluder.shop.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitServiceImpl implements RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Value("${rate.limit.requests}")
    private int requestsPerMinute;

    @Value("${rate.limit.duration}")
    private int durationInSeconds;


    public boolean tryConsume(String key, int tokens) {
        Bucket bucket = getOrCreateBucket(key);
        return bucket.tryConsume(tokens);
    }

    public long getAvailableTokens(String key) {
        Bucket bucket = getOrCreateBucket(key);
        return bucket.getAvailableTokens();
    }

    private Bucket getOrCreateBucket(String key) {
        return buckets.computeIfAbsent(key, k -> createBucket());
    }

    private Bucket createBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(requestsPerMinute)
                        .refillIntervally(requestsPerMinute, Duration.ofSeconds(durationInSeconds))
                        .build())
                .build();
    }
}