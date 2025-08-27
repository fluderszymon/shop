package com.szymonfluder.shop.exception;

public class UsernameTakenException extends RuntimeException {

    public UsernameTakenException(String username) {
        super("Username " + username + " is already taken");
    }
}