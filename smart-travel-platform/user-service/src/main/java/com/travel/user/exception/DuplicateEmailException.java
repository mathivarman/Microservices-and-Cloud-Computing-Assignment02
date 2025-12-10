package com.travel.user.exception;

/**
 * Exception thrown when trying to create a user with duplicate email
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("User already exists with email: " + email);
    }
}
