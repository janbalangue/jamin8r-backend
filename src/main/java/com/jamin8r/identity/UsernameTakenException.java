package com.jamin8r.identity;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String username) {
        super("Username is already taken: " + username);
    }
}
