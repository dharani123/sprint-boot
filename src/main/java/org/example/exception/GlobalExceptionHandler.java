package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
// This class intercepts exceptions thrown by ANY controller in the app.
// Think of it as a safety net stretched across all your controllers.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles: StudentNotFoundException thrown anywhere in the app
    // → Always returns 404 Not Found with a clear message
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(
            StudentNotFoundException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),   // 404
            "Not Found",
            ex.getMessage(),                // "No student found with id: 999"
            request.getRequestURI()         // "/api/students/999"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Handles: DuplicateEmailException
    // → Always returns 409 Conflict
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(
            DuplicateEmailException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),    // 409
            "Conflict",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Handles: @Valid failures — fired when any validation annotation fails
    // Returns 400 with a map of { fieldName → errorMessage } for every failing field
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        // Collect all field errors into a map: { "name" → "Name cannot be blank", ... }
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing  // keep first message if field has multiple errors
                ));

        ValidationErrorResponse error = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Input validation failed for " + fieldErrors.size() + " field(s)",
            request.getRequestURI(),
            fieldErrors
        );
        return ResponseEntity.badRequest().body(error);
    }

    // Handles: malformed JSON body  e.g. POST with "bad json{{"
    // → Returns 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(), // 400
            "Bad Request",
            "Request body is missing or contains invalid JSON",
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }

    // Handles: wrong type in URL path  e.g. GET /api/students/abc (not a number)
    // → Returns 400 Bad Request
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String message = String.format(
            "Parameter '%s' must be of type %s but got: '%s'",
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown",
            ex.getValue()
        );
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            message,
            request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(error);
    }

    // Catch-all: any exception we haven't specifically handled
    // → Returns 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
            "Internal Server Error",
            "An unexpected error occurred: " + ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.internalServerError().body(error);
    }
}
