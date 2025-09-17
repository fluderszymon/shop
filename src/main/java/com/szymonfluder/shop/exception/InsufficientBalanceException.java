package com.szymonfluder.shop.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal requiredAmount) {
        super("Insufficient balance. Current: " + currentBalance + ", Required: " + requiredAmount);
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }
}