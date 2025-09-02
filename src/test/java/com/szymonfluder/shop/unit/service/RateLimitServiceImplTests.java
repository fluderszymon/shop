package com.szymonfluder.shop.unit.service;

import com.szymonfluder.shop.security.RateLimitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceImplTests {

    @InjectMocks
    private RateLimitServiceImpl rateLimitService;

    private static final String TEST_KEY = "test-key";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rateLimitService, "requestsPerMinute", 5);
        ReflectionTestUtils.setField(rateLimitService, "durationInSeconds", 1);
    }

    @Test
    void tryConsume_shouldReturnTrue_whenTokensAvailable() {
        boolean result = rateLimitService.tryConsume(TEST_KEY, 1);
        
        assertThat(result).isTrue();
    }

    @Test
    void tryConsume_shouldReturnFalse_whenNoTokensAvailable() {
        rateLimitService.tryConsume(TEST_KEY, 5);
        
        boolean result = rateLimitService.tryConsume(TEST_KEY, 1);
        
        assertThat(result).isFalse();
    }

    @Test
    void tryConsume_shouldReturnFalse_whenRequestingMoreTokensThanAvailable() {
        rateLimitService.tryConsume(TEST_KEY, 3);
        
        boolean result = rateLimitService.tryConsume(TEST_KEY, 3);
        
        assertThat(result).isFalse();
    }

    @Test
    void getAvailableTokens_shouldReturnCorrectCount_whenNoTokensConsumed() {
        long availableTokens = rateLimitService.getAvailableTokens(TEST_KEY);
        
        assertThat(availableTokens).isEqualTo(5);
    }

    @Test
    void getAvailableTokens_shouldReturnCorrectCount_afterConsumingTokens() {
        rateLimitService.tryConsume(TEST_KEY, 2);
        
        long availableTokens = rateLimitService.getAvailableTokens(TEST_KEY);
        
        assertThat(availableTokens).isEqualTo(3);
    }

    @Test
    void getAvailableTokens_shouldReturnZero_whenAllTokensConsumed() {
        rateLimitService.tryConsume(TEST_KEY, 5);
        
        long availableTokens = rateLimitService.getAvailableTokens(TEST_KEY);
        
        assertThat(availableTokens).isEqualTo(0);
    }

    @Test
    void tryConsume_shouldHandleMultipleKeysIndependently() {
        String key1 = "client-1";
        String key2 = "client-2";
        
        rateLimitService.tryConsume(key1, 3);
        rateLimitService.tryConsume(key2, 2);
        
        long tokens1 = rateLimitService.getAvailableTokens(key1);
        long tokens2 = rateLimitService.getAvailableTokens(key2);
        
        assertThat(tokens1).isEqualTo(2);
        assertThat(tokens2).isEqualTo(3);
    }

    @Test
    void tryConsume_shouldReturnFalse_whenRequestingZeroTokens() {
        boolean result = rateLimitService.tryConsume(TEST_KEY, 0);
        
        assertThat(result).isFalse();
    }

    @Test
    void tryConsume_shouldReturnFalse_whenRequestingNegativeTokens() {
        boolean result = rateLimitService.tryConsume(TEST_KEY, -1);
        
        assertThat(result).isFalse();
    }
}
