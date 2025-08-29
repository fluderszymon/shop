package com.szymonfluder.shop.unit.service;

import com.szymonfluder.shop.security.JWTServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class JWTServiceImplTests {

    @Autowired
    private JWTServiceImpl jwtService;

    @MockitoBean
    private UserDetails userDetails;

    @MockitoBean
    private HttpServletRequest request;

    @Test
    void generateToken_shouldReturnValidToken() {
        String givenUsername = "username";
        String token = jwtService.generateToken(givenUsername);
        
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void extractUsername_shouldExtractUsernameFromValidToken() {
        String username = "username";
        String token = jwtService.generateToken(username);

        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void validateToken_shouldReturnTrueForValidTokenAndUser() {
        String username = "username";
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(username);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_shouldReturnFalseForWrongUsername() {
        String username = "username";
        String wrongUsername = "wronguser";
        String token = jwtService.generateToken(username);
        when(userDetails.getUsername()).thenReturn(wrongUsername);

        boolean isValid = jwtService.validateToken(token, userDetails);
        assertThat(isValid).isFalse();
    }

    @Test
    void extractTokenFromHeader_shouldExtractTokenFromValidHeader() {
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expectedToken);

        String extractedToken = jwtService.extractTokenFromHeader(request);
        
        assertThat(extractedToken).isEqualTo(expectedToken);
    }

    @Test
    void extractTokenFromHeader_shouldThrowExceptionForMissingHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> jwtService.extractTokenFromHeader(request));
        assertThat(exception.getMessage()).isEqualTo("Invalid or missing Authorization header");
    }

    @Test
    void extractTokenFromHeader_shouldThrowExceptionForHeaderWithoutBearer() {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> jwtService.extractTokenFromHeader(request));
        assertThat(exception.getMessage()).isEqualTo("Invalid or missing Authorization header");
    }
}