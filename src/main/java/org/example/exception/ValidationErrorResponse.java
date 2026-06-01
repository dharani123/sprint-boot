package org.example.exception;

import java.util.Map;

// Extends ErrorResponse to add per-field validation error details
// Response shape:
// {
//   "status": 400,
//   "error": "Validation Failed",
//   "message": "Input validation failed for 2 field(s)",
//   "path": "/api/students",
//   "timestamp": "...",
//   "fieldErrors": {
//     "name":  "Name cannot be blank",
//     "email": "Must be a valid email address"
//   }
// }
public class ValidationErrorResponse extends ErrorResponse {

    private Map<String, String> fieldErrors;

    public ValidationErrorResponse(int status, String error, String message,
                                   String path, Map<String, String> fieldErrors) {
        super(status, error, message, path);
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() { return fieldErrors; }
}
