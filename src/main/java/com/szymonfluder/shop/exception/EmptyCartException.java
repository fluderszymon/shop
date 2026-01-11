package com.szymonfluder.shop.exception;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException() {
        super("Cart is empty. Add products before checkout");
    }

    public EmptyCartException(String message) {
        super(message);
    }
}