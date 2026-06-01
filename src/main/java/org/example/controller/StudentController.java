package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.StudentRequestDTO;
import org.example.dto.StudentResponseDTO;
import org.example.service.StudentService;
import org.springframework.data.domain.Page;
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
    public List<StudentResponseDTO> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public StudentResponseDTO getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    // @RequestBody maps to StudentRequestDTO — no `id` field, so clients can never inject their own ID
    @PostMapping
    public ResponseEntity<StudentResponseDTO> addStudent(@Valid @RequestBody StudentRequestDTO dto) {
        return ResponseEntity.status(201).body(studentService.addStudent(dto));
    }

    @PutMapping("/{id}")
    public StudentResponseDTO updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO dto) {
        return studentService.updateStudent(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{course}")
    public List<StudentResponseDTO> findByCourse(@PathVariable String course) {
        return studentService.findByCourse(course);
    }

    @GetMapping("/search")
    public List<StudentResponseDTO> search(@RequestParam String keyword) {
        return studentService.searchByName(keyword);
    }

    @GetMapping("/count")
    public long getCount() {
        return studentService.getCount();
    }

    @GetMapping("/paged")
    public Page<StudentResponseDTO> getStudentsPaged(
            @RequestParam(defaultValue = "0")   int    page,
            @RequestParam(defaultValue = "3")   int    size,
            @RequestParam(defaultValue = "id")  String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return studentService.getStudentsPaged(page, size, sortBy, direction);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<StudentResponseDTO>> enrollBulk(@RequestBody List<@Valid StudentRequestDTO> dtos) {
        return ResponseEntity.status(201).body(studentService.enrollBulk(dtos));
    }
}
