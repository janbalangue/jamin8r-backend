package com.jamin8r.social;

public class SelfFollowException extends RuntimeException {
    public SelfFollowException() {
        super("A user cannot follow themselves");
    }
}
