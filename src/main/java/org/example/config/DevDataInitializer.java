package org.example.config;

import org.example.model.Student;
import org.example.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// @Slf4j generates: private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);
// You just use `log` directly — no import, no declaration
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor  // generates constructor for all `final` fields → replaces our manual constructor
public class DevDataInitializer implements CommandLineRunner {

    // @RequiredArgsConstructor sees this final field and generates:
    // public DevDataInitializer(StudentRepository studentRepository) { this.studentRepository = studentRepository; }
    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) {
        log.info(">>> DEV PROFILE: Seeding sample student data...");

        studentRepository.save(new Student("Dharani",  "dharani@example.com",  "Spring Boot"));
        studentRepository.save(new Student("Ravi",     "ravi@example.com",     "Java"));
        studentRepository.save(new Student("Priya",    "priya@example.com",    "Java"));
        studentRepository.save(new Student("Kiran",    "kiran@example.com",    "React"));
        studentRepository.save(new Student("Ananya",   "ananya@example.com",   "Spring Boot"));

        log.info(">>> DEV PROFILE: {} students seeded.", studentRepository.count());
    }
}
