package org.example.exception;

import java.time.LocalDateTime;

// This is the JSON shape returned for every error in our app
// Instead of Spring's generic error, every error now looks like:
// {
//   "status": 404,
//   "error": "Not Found",
//   "message": "No student found with id: 999",
//   "path": "/api/students/999",
//   "timestamp": "2024-01-01T10:00:00"
// }
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message, String path) {
        this.status    = status;
        this.error     = error;
        this.message   = message;
        this.path      = path;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus()             { return status; }
    public String getError()           { return error; }
    public String getMessage()         { return message; }
    public String getPath()            { return path; }
    public LocalDateTime getTimestamp(){ return timestamp; }
}
