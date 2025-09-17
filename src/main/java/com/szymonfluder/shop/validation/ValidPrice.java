package com.szymonfluder.shop.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidPriceValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrice {
    
    String message() default "Price must be a valid positive number with up to 2 decimal places between 0.01 and 999999.99";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
