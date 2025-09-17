package com.szymonfluder.shop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    error -> error.getField(),
                    error -> error.getDefaultMessage(),
                    (existing, replacement) -> existing + "; " + replacement
                ));
        
        return ResponseEntity.badRequest()
                .body(createValidationErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Validation Failed",
                    "Request validation failed",
                    errors
                ));
    }

    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameTaken(UsernameTakenException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse(
                    HttpStatus.CONFLICT,
                    "Conflict",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(
                    HttpStatus.NOT_FOUND,
                    "Not Found",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientBalance(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient Balance",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<Map<String, Object>> handleOutOfStock(OutOfStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Out of Stock",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyCart(EmptyCartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Empty Cart",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid ID format. ID must be a number";
        
        if (ex.getName().contains("Id")) {
            message = "Invalid " + ex.getName() + " format. Must be a number";
        }
        
        return ResponseEntity.badRequest()
                .body(createErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Bad Request",
                    message
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(createErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Bad Request",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(OrderAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleOrderAccessDenied(OrderAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse(
                    HttpStatus.FORBIDDEN,
                    "Forbidden",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(CartAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleCartAccessDenied(CartAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse(
                    HttpStatus.FORBIDDEN,
                    "Forbidden",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse(
                    HttpStatus.FORBIDDEN,
                    "Forbidden",
                    ex.getMessage()
                ));
    }

    private Map<String, Object> createErrorResponse(HttpStatus status, String error, String message) {
        return Map.of(
            "timestamp", LocalDateTime.now(),
            "status", status.value(),
            "error", error,
            "message", message
        );
    }

    private Map<String, Object> createValidationErrorResponse(HttpStatus status, String error, String message, Object errors) {
        return Map.of(
            "timestamp", LocalDateTime.now(),
            "status", status.value(),
            "error", error,
            "message", message,
            "errors", errors
        );
    }
}