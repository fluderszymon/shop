package com.szymonfluder.shop.exception;

public class CartAccessDeniedException extends RuntimeException {
    
    public CartAccessDeniedException(String message) {
        super(message);
    }
}