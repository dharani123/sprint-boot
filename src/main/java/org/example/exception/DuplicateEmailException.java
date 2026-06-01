package org.example.exception;

// Thrown when someone tries to register a student with an email that already exists
public class DuplicateEmailException extends RuntimeException {

    private final String email;

    public DuplicateEmailException(String email) {
        super("A student with email '" + email + "' already exists");
        this.email = email;
    }

    public String getEmail() { return email; }
}
