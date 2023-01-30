package com.oakenscience.todoapp.error;

public final class UserActivationFailed extends RuntimeException {
    public UserActivationFailed(final String message) {
        super(message);
    }
}
