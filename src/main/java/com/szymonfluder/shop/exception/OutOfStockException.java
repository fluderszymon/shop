package com.szymonfluder.shop.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String productName, int availableStock, int requestedQuantity) {
        super("Not enough stock for product '" + productName + "'. Available: " + availableStock + ", Requested: " + requestedQuantity);
    }

    public OutOfStockException(int productId, int availableStock, int requestedQuantity) {
        super("Not enough stock for product ID " + productId + ". Available: " + availableStock + ", Requested: " + requestedQuantity);
    }

    public OutOfStockException(String message) {
        super(message);
    }

}