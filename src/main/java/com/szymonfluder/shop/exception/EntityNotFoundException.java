package com.szymonfluder.shop.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityType, int id) {
        super(entityType + " with id " + id + " not found");
    }

    public EntityNotFoundException(String entityType, String identifier) {
        super(entityType + " with identifier '" + identifier + "' not found");
    }
}