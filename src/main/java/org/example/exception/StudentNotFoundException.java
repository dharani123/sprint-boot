package org.example.exception;

// Extends RuntimeException — no need to declare it in method signatures
// We throw this from the Service layer when a student ID doesn't exist
public class StudentNotFoundException extends RuntimeException {

    private final Long studentId;

    public StudentNotFoundException(Long id) {
        super("No student found with id: " + id);
        this.studentId = id;
    }

    public Long getStudentId() { return studentId; }
}
