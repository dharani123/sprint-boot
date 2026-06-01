package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Student;

// What WE send back to the client — we control every field.
// This breaks the coupling between your DB schema and your API contract:
//   - Rename a DB column? Update the entity + mapper. The API response shape stays the same.
//   - Add a sensitive field to the entity (e.g. passwordHash)? It won't appear here unless you add it.
//   - Need a computed field that doesn't exist in the DB? Add it here.
@Data
@NoArgsConstructor
public class StudentResponseDTO {

    private Long   id;
    private String name;
    private String email;
    private String course;

    // A computed field — does not exist in the Student table.
    // This is only possible because we have a separate response shape.
    // If we returned the @Entity directly, we couldn't add fields that aren't DB columns.
    private String displayLabel;

    // Explicit constructor instead of @AllArgsConstructor — eliminates the IDE red squiggle
    // that appears when IntelliJ can't see Lombok-generated constructors without the plugin.
    public StudentResponseDTO(Long id, String name, String email, String course, String displayLabel) {
        this.id           = id;
        this.name         = name;
        this.email        = email;
        this.course       = course;
        this.displayLabel = displayLabel;
    }

    // Static factory method: converts a Student entity → this DTO.
    // All mapping logic lives in one place. Change the entity? Fix it here, API stays stable.
    public static StudentResponseDTO fromEntity(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getCourse(),
                student.getName() + " → " + student.getCourse()
        );
    }
}
