package com.szymonfluder.shop.unit.validation;

import com.szymonfluder.shop.validation.StrongPasswordValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class StrongPasswordValidatorTests {

    private StrongPasswordValidator validator;
    
    @Mock
    private ConstraintValidatorContext context;
    
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new StrongPasswordValidator();
        
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        lenient().when(violationBuilder.addConstraintViolation()).thenReturn(context);
    }

    private boolean isValidPassword(String password) {
        return validator.isValid(password, context);
    }

    @Test
    void isValidPassword_shouldReturnTrue_whenPasswordIsStrong() {
        String strongPassword = "MyStr0ng!Pass";
        boolean result = isValidPassword(strongPassword);
        assertThat(result).isTrue();
    }

    @Test
    void isValidPassword_shouldReturnTrue_whenPasswordHasSpecialChars() {
        String passwordWithSpecial = "MyStrongPassword0@";
        boolean result = isValidPassword(passwordWithSpecial);
        assertThat(result).isTrue();
    }

    @Test
    void isValidPassword_shouldReturnFalse_whenPasswordIsTooShort() {
        String shortPassword = "My1!";
        boolean result = isValidPassword(shortPassword);
        assertThat(result).isFalse();
    }

    @Test
    void isValidPassword_shouldReturnFalse_whenPasswordHasNoUppercase() {
        String noUppercase = "mystrongpassword0!";
        boolean result = isValidPassword(noUppercase);
        assertThat(result).isFalse();
    }

    @Test
    void isValidPassword_shouldReturnFalse_whenPasswordHasNoLowercase() {
        String noLowercase = "MYSTRONGPASSWORD0!";
        boolean result = isValidPassword(noLowercase);
        assertThat(result).isFalse();
    }

    @Test
    void isValidPassword_shouldReturnFalse_whenPasswordHasNoDigit() {
        String noDigit = "MyStrongPassword!";
        boolean result = isValidPassword(noDigit);
        assertThat(result).isFalse();
    }

    @Test
    void isValidPassword_shouldReturnFalse_whenPasswordHasNoSpecialCharacter() {
        String noSpecial = "MyStrongPassword0";
        boolean result = isValidPassword(noSpecial);
        assertThat(result).isFalse();
    }

    @Test
    void isValidPassword_shouldReturnFalse_whenPasswordHasWhitespace() {
        String withWhitespace = "My StrongPassword!0";
        boolean result = isValidPassword(withWhitespace);
        assertThat(result).isFalse();
    }

    @Test
    void isValidPassword_shouldReturnFalse_whenPasswordIsNull() {
        boolean result = isValidPassword(null);
        assertThat(result).isFalse();
    }

    @Test
    void isValidPassword_shouldReturnFalse_whenPasswordIsEmpty() {
        boolean result = isValidPassword("");
        assertThat(result).isFalse();
    }

    @Test
    void isValidPassword_shouldReturnFalse_whenPasswordTooLong() {
        String tooLong = "A".repeat(21) + "1!";
        boolean result = isValidPassword(tooLong);
        assertThat(result).isFalse();
    }

    @Test
    void isValidPassword_shouldReturnTrue_whenPasswordExactlyMinLength() {
        String minLength = "MyStro0!";
        boolean result = isValidPassword(minLength);
        assertThat(result).isTrue();
    }
}