package org.example.service;

import org.example.exception.DuplicateEmailException;
import org.example.exception.StudentNotFoundException;
import org.example.model.Student;
import org.example.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Now THROWS instead of returning Optional — controller becomes much cleaner
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        // If not found → throws StudentNotFoundException
        // GlobalExceptionHandler catches it → returns 404 JSON automatically
    }

    public Student addStudent(Student student) {
        // Check for duplicate email before saving
        boolean emailExists = studentRepository.findAll().stream()
                .anyMatch(s -> s.getEmail().equalsIgnoreCase(student.getEmail()));

        if (emailExists) {
            throw new DuplicateEmailException(student.getEmail());
            // GlobalExceptionHandler catches it → returns 409 Conflict JSON
        }
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student updated) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setCourse(updated.getCourse());
        return studentRepository.save(existing);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }

    public List<Student> findByCourse(String course) {
        return studentRepository.findByCourse(course);
    }

    public List<Student> searchByName(String keyword) {
        return studentRepository.findByNameContainingIgnoreCase(keyword);
    }
}
