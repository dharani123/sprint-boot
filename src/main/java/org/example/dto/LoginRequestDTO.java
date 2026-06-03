package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    // Single field accepts both email and mobile — we try both in the service
    @NotBlank(message = "Email or mobile number is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;
}
