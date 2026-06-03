package org.example.exception;

public class DuplicateMobileException extends RuntimeException {
    public DuplicateMobileException(String message) {
        super(message);
    }
}
