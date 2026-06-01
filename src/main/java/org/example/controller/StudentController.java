package org.example.controller;

import jakarta.validation.Valid;
import org.example.model.Student;
import org.example.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // No more Optional — service throws StudentNotFoundException if not found
    // GlobalExceptionHandler converts that exception → 404 JSON automatically
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(@Valid @RequestBody Student student) {
        // @Valid triggers validation of all annotations on Student fields BEFORE this method runs
        // If any annotation fails → MethodArgumentNotValidException is thrown automatically
        // GlobalExceptionHandler catches it → returns 400 with field-level errors
        return ResponseEntity.status(201).body(studentService.addStudent(student));
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @Valid @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/course/{course}")
    public List<Student> findByCourse(@PathVariable String course) {
        return studentService.findByCourse(course);
    }

    @GetMapping("/search")
    public List<Student> search(@RequestParam String keyword) {
        return studentService.searchByName(keyword);
    }
}
