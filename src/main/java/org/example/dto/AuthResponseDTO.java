package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;   // JWT — frontend stores this and sends it with every request
    private String name;
    private String email;
}
