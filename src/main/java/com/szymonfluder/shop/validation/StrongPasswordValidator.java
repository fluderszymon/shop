package com.szymonfluder.shop.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 20;
    
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String DIGIT_PATTERN = ".*[0-9].*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";
    private static final String WHITESPACE_PATTERN = ".*\\s.*";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        List<String> errors = validatePassword(password);
        
        if (!errors.isEmpty()) {
            String messageTemplate = String.join(", ", errors);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(messageTemplate)
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }

    private List<String> validatePassword(String password) {
        return Stream.of(
            checkLength(password),
            checkUppercase(password),
            checkLowercase(password),
            checkDigit(password),
            checkSpecialChar(password),
            checkWhitespace(password)
        ).filter(s -> !s.isEmpty())
         .toList();
    }

    private String checkLength(String password) {
        if (password.length() < MIN_LENGTH) {
            return "Password must be at least " + MIN_LENGTH + " characters long";
        }
        if (password.length() > MAX_LENGTH) {
            return "Password must not exceed " + MAX_LENGTH + " characters";
        }
        return "";
    }

    private String checkUppercase(String password) {
        return password.matches(UPPERCASE_PATTERN) ? "" : 
               "Password must contain at least one uppercase letter";
    }

    private String checkLowercase(String password) {
        return password.matches(LOWERCASE_PATTERN) ? "" : 
               "Password must contain at least one lowercase letter";
    }

    private String checkDigit(String password) {
        return password.matches(DIGIT_PATTERN) ? "" : 
               "Password must contain at least one digit";
    }

    private String checkSpecialChar(String password) {
        return password.matches(SPECIAL_CHAR_PATTERN) ? "" : 
               "Password must contain at least one special character";
    }

    private String checkWhitespace(String password) {
        return password.matches(WHITESPACE_PATTERN) ? 
               "Password must not contain whitespace characters" : "";
    }
}