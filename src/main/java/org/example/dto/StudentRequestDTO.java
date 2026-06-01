package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// What the CLIENT sends to us — the shape WE define for our API contract.
// Crucially, there is NO `id` field here. Clients never get to dictate their own ID.
// The DB assigns the ID via @GeneratedValue. Even if a client sends {"id": 99, ...},
// Jackson will simply ignore it because this DTO has no `id` field to map it to.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Course cannot be blank")
    @Size(min = 2, max = 100, message = "Course must be between 2 and 100 characters")
    private String course;
}
