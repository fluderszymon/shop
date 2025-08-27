package com.szymonfluder.shop.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    String generateToken(String username);
    boolean validateToken(String token, UserDetails userDetails);
    String extractUsername(String token);
}
