package com.travel.user.exception;

/**
 * Exception thrown when user is not found
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }
}
