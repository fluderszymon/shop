package com.szymonfluder.shop.exception;

public class OrderAccessDeniedException extends RuntimeException {
    
    public OrderAccessDeniedException(String message) {
        super(message);
    }
}