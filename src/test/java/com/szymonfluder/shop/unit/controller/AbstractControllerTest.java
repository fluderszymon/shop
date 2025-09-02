package com.szymonfluder.shop.unit.controller;

import com.szymonfluder.shop.security.ResourceAccessService;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.security.RateLimitService;
import com.szymonfluder.shop.security.UserDetailsServiceImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractControllerTest {

    protected final String VALID_JWT = "valid.jwt.token";
    protected final String AUTH_HEADER = "Bearer " + VALID_JWT;

    protected UserDetails createTestUserDetails() {
        return org.springframework.security.core.userdetails.User.builder()
                .username("username")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("USER")))
                .build();
    }

    protected void setupJwtMocksWithTokenExtraction(JWTService jwtService, UserDetailsServiceImpl userDetailsService) {
        UserDetails userDetails = createTestUserDetails();
        
        when(jwtService.extractTokenFromHeader(any(HttpServletRequest.class))).thenReturn(VALID_JWT);
        when(jwtService.extractUsername(VALID_JWT)).thenReturn("username");
        when(jwtService.validateToken(VALID_JWT, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails);
    }

    
    protected void setupRateLimitMocks(RateLimitService rateLimitService) {
        when(rateLimitService.tryConsume(any(String.class), any(Integer.class))).thenReturn(true);
        when(rateLimitService.getAvailableTokens(any(String.class))).thenReturn(59L);
    }

    protected void setupResourceAccessServiceMocks(ResourceAccessService resourceAccessService) {
        when(resourceAccessService.isOwnerOrAdmin(any(Integer.class))).thenReturn(true);
    }
}