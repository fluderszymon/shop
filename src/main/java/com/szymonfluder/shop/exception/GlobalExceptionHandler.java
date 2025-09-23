package com.szymonfluder.shop.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${error.show-stack-trace:false}")
    private boolean showStackTrace;

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
                .body(createErrorResponse(
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
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse(
                    HttpStatus.CONFLICT,
                    "Insufficient Balance",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<Map<String, Object>> handleOutOfStock(OutOfStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse(
                    HttpStatus.CONFLICT,
                    "Out of Stock",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyCart(EmptyCartException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse(
                    HttpStatus.CONFLICT,
                    "Empty Cart",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse(
                    HttpStatus.FORBIDDEN,
                    "Access Denied",
                    ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalServerError(Exception ex) {
        String message = showStackTrace ? 
            ex.getMessage() + "\n" + getStackTrace(ex) : 
            "An unexpected error occurred. Please try again later.";
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal Server Error",
                    message
                ));
    }

    private Map<String, Object> createErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> createErrorResponse(HttpStatus status, String error, String message, Object errors) {
        Map<String, Object> response = createErrorResponse(status, error, message);
        response.put("errors", errors);
        return response;
    }

    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}