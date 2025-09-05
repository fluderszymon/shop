package com.szymonfluder.shop.security;

public interface RateLimitService {

    boolean tryConsume(String key, int tokens);
    long getAvailableTokens(String key);

}