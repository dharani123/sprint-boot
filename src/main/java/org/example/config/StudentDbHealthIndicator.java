package org.example.config;

import org.example.repository.StudentRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

// Any @Component that implements HealthIndicator is automatically picked up
// by /actuator/health and shown under its own named section.
// The name comes from the class: "StudentDbHealthIndicator" → "studentDb"
@Component
public class StudentDbHealthIndicator implements HealthIndicator {

    private final StudentRepository studentRepository;

    public StudentDbHealthIndicator(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Health health() {
        try {
            long studentCount = studentRepository.count();

            if (studentCount == 0) {
                // UP but with a warning — DB works but has no data
                return Health.up()
                        .withDetail("status", "Database reachable but empty")
                        .withDetail("studentCount", studentCount)
                        .build();
            }

            // Fully healthy
            return Health.up()
                    .withDetail("status", "Database reachable")
                    .withDetail("studentCount", studentCount)
                    .build();

        } catch (Exception e) {
            // DOWN — something is wrong with the DB connection
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
