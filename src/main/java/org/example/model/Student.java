package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter             // generates: getId(), getName(), getEmail(), getCourse()
@Setter             // generates: setId(), setName(), setEmail(), setCourse()
@NoArgsConstructor  // generates: public Student() {}  ← required by Hibernate
@AllArgsConstructor // generates: public Student(Long id, String name, String email, String course)
@ToString           // generates: toString() showing all field values — great for logging
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Course cannot be blank")
    @Size(min = 2, max = 100, message = "Course must be between 2 and 100 characters")
    private String course;

    // Custom constructor still works alongside Lombok-generated ones
    public Student(String name, String email, String course) {
        this.name   = name;
        this.email  = email;
        this.course = course;
    }

    // ↑ That's it. No getters. No setters. Lombok generates them all at compile time.
}
