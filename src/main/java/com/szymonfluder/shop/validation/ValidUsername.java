package com.szymonfluder.shop.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
    
    String message() default "Username must be between 3 and 20 characters long and contain only letters, numbers, underscores and start with a letter";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}