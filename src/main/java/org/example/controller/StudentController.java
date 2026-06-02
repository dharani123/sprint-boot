package org.example.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.StudentRequestDTO;
import org.example.dto.StudentResponseDTO;
import org.example.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentResponseDTO> getAllStudents() {
        log.info("→ [StudentController] GET /api/students");
        List<StudentResponseDTO> result = studentService.getAllStudents();
        log.info("← [StudentController] GET /api/students → returning {} students", result.size());
        return result;
    }

    @GetMapping("/{id}")
    public StudentResponseDTO getStudentById(@PathVariable Long id) {
        log.info("→ [StudentController] GET /api/students/{}", id);
        StudentResponseDTO result = studentService.getStudentById(id);
        log.info("← [StudentController] GET /api/students/{} → returning '{}'", id, result.getDisplayLabel());
        return result;
    }

    @PostMapping
    public ResponseEntity<StudentResponseDTO> addStudent(@Valid @RequestBody StudentRequestDTO dto) {
        log.info("→ [StudentController] POST /api/students, body: name='{}', email='{}'", dto.getName(), dto.getEmail());
        StudentResponseDTO result = studentService.addStudent(dto);
        log.info("← [StudentController] POST /api/students → 201 Created, id={}", result.getId());
        return ResponseEntity.status(201).body(result);
    }

    @PutMapping("/{id}")
    public StudentResponseDTO updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO dto) {
        log.info("→ [StudentController] PUT /api/students/{}, body: name='{}'", id, dto.getName());
        StudentResponseDTO result = studentService.updateStudent(id, dto);
        log.info("← [StudentController] PUT /api/students/{} → updated '{}'", id, result.getDisplayLabel());
        return result;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        log.info("→ [StudentController] DELETE /api/students/{}", id);
        studentService.deleteStudent(id);
        log.info("← [StudentController] DELETE /api/students/{} → 204 No Content", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{course}")
    public List<StudentResponseDTO> findByCourse(@PathVariable String course) {
        log.info("→ [StudentController] GET /api/students/course/{}", course);
        List<StudentResponseDTO> result = studentService.findByCourse(course);
        log.info("← [StudentController] GET /api/students/course/{} → {} students", course, result.size());
        return result;
    }

    @GetMapping("/search")
    public List<StudentResponseDTO> search(@RequestParam String keyword) {
        log.info("→ [StudentController] GET /api/students/search?keyword='{}'", keyword);
        List<StudentResponseDTO> result = studentService.searchByName(keyword);
        log.info("← [StudentController] GET /api/students/search → {} results", result.size());
        return result;
    }

    @GetMapping("/count")
    public long getCount() {
        log.info("→ [StudentController] GET /api/students/count");
        long count = studentService.getCount();
        log.info("← [StudentController] GET /api/students/count → {}", count);
        return count;
    }

    @GetMapping("/paged")
    public Page<StudentResponseDTO> getStudentsPaged(
            @RequestParam(defaultValue = "0")   int    page,
            @RequestParam(defaultValue = "3")   int    size,
            @RequestParam(defaultValue = "id")  String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        log.info("→ [StudentController] GET /api/students/paged?page={}&size={}&sortBy={}&direction={}", page, size, sortBy, direction);
        Page<StudentResponseDTO> result = studentService.getStudentsPaged(page, size, sortBy, direction);
        log.info("← [StudentController] GET /api/students/paged → page {}/{}, {} items on this page", result.getNumber(), result.getTotalPages() - 1, result.getNumberOfElements());
        return result;
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<StudentResponseDTO>> enrollBulk(@RequestBody List<@Valid StudentRequestDTO> dtos) {
        log.info("→ [StudentController] POST /api/students/bulk, {} students in request", dtos.size());
        List<StudentResponseDTO> result = studentService.enrollBulk(dtos);
        log.info("← [StudentController] POST /api/students/bulk → 201 Created, {} students saved", result.size());
        return ResponseEntity.status(201).body(result);
    }
}
