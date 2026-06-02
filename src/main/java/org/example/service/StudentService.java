package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.StudentRequestDTO;
import org.example.dto.StudentResponseDTO;
import org.example.exception.DuplicateEmailException;
import org.example.exception.StudentNotFoundException;
import org.example.model.Student;
import org.example.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        log.info("  [StudentService] getAllStudents() ─ entry");

        log.info("    [repo] calling studentRepository.findAll()");
        List<Student> rows = studentRepository.findAll();
        log.info("    [repo] findAll() returned {} rows", rows.size());

        List<StudentResponseDTO> result = rows.stream().map(StudentResponseDTO::fromEntity).toList();
        log.info("  [StudentService] getAllStudents() ─ exit → {} DTOs mapped", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        log.info("  [StudentService] getStudentById({}) ─ entry", id);

        log.info("    [repo] calling studentRepository.findById({})", id);
        Optional<Student> found = studentRepository.findById(id);

        if (found.isEmpty()) {
            log.warn("    [repo] findById({}) returned Optional.empty → throwing StudentNotFoundException", id);
            throw new StudentNotFoundException(id);
        }

        log.info("    [repo] findById({}) returned Student: name='{}'", id, found.get().getName());
        StudentResponseDTO result = StudentResponseDTO.fromEntity(found.get());
        log.info("  [StudentService] getStudentById({}) ─ exit → '{}'", id, result.getDisplayLabel());
        return result;
    }

    @Transactional
    public StudentResponseDTO addStudent(StudentRequestDTO dto) {
        log.info("  [StudentService] addStudent() ─ entry, name='{}', email='{}'", dto.getName(), dto.getEmail());

        log.info("    [repo] calling studentRepository.existsByEmail('{}')", dto.getEmail());
        boolean exists = studentRepository.existsByEmail(dto.getEmail());
        log.info("    [repo] existsByEmail() returned {}", exists);

        if (exists) {
            log.warn("  [StudentService] duplicate email '{}' → throwing DuplicateEmailException", dto.getEmail());
            throw new DuplicateEmailException(dto.getEmail());
        }

        Student student = new Student(dto.getName(), dto.getEmail(), dto.getCourse());
        log.info("    [repo] calling studentRepository.save(Student{{ name='{}' }})", student.getName());
        Student saved = studentRepository.save(student);
        log.info("    [repo] save() returned Student{{ id={}, name='{}' }}", saved.getId(), saved.getName());

        StudentResponseDTO result = StudentResponseDTO.fromEntity(saved);
        log.info("  [StudentService] addStudent() ─ exit → Student created with id={}", result.getId());
        return result;
    }

    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO dto) {
        log.info("  [StudentService] updateStudent({}) ─ entry, new name='{}'", id, dto.getName());

        log.info("    [repo] calling studentRepository.findById({})", id);
        Optional<Student> found = studentRepository.findById(id);

        if (found.isEmpty()) {
            log.warn("    [repo] findById({}) returned Optional.empty → throwing StudentNotFoundException", id);
            throw new StudentNotFoundException(id);
        }

        log.info("    [repo] findById({}) returned Student: name='{}'", id, found.get().getName());
        Student existing = found.get();
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setCourse(dto.getCourse());

        log.info("    [repo] calling studentRepository.save(Student{{ id={}, name='{}' }})", id, existing.getName());
        Student saved = studentRepository.save(existing);
        log.info("    [repo] save() completed for id={}", saved.getId());

        StudentResponseDTO result = StudentResponseDTO.fromEntity(saved);
        log.info("  [StudentService] updateStudent({}) ─ exit → '{}'", id, result.getDisplayLabel());
        return result;
    }

    @Transactional
    public void deleteStudent(Long id) {
        log.info("  [StudentService] deleteStudent({}) ─ entry", id);

        log.info("    [repo] calling studentRepository.existsById({})", id);
        boolean exists = studentRepository.existsById(id);
        log.info("    [repo] existsById({}) returned {}", id, exists);

        if (!exists) {
            log.warn("  [StudentService] id={} not found → throwing StudentNotFoundException", id);
            throw new StudentNotFoundException(id);
        }

        log.info("    [repo] calling studentRepository.deleteById({})", id);
        studentRepository.deleteById(id);
        log.info("    [repo] deleteById({}) completed", id);

        log.info("  [StudentService] deleteStudent({}) ─ exit → deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> findByCourse(String course) {
        log.info("  [StudentService] findByCourse('{}') ─ entry", course);

        log.info("    [repo] calling studentRepository.findByCourse('{}')", course);
        List<Student> rows = studentRepository.findByCourse(course);
        log.info("    [repo] findByCourse('{}') returned {} rows", course, rows.size());

        List<StudentResponseDTO> result = rows.stream().map(StudentResponseDTO::fromEntity).toList();
        log.info("  [StudentService] findByCourse('{}') ─ exit → {} results", course, result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> searchByName(String keyword) {
        log.info("  [StudentService] searchByName('{}') ─ entry", keyword);

        log.info("    [repo] calling studentRepository.findByNameContainingIgnoreCase('{}')", keyword);
        List<Student> rows = studentRepository.findByNameContainingIgnoreCase(keyword);
        log.info("    [repo] findByNameContainingIgnoreCase('{}') returned {} rows", keyword, rows.size());

        List<StudentResponseDTO> result = rows.stream().map(StudentResponseDTO::fromEntity).toList();
        log.info("  [StudentService] searchByName('{}') ─ exit → {} results", keyword, result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public long getCount() {
        log.info("  [StudentService] getCount() ─ entry");

        log.info("    [repo] calling studentRepository.count()");
        long count = studentRepository.count();
        log.info("    [repo] count() returned {}", count);

        log.info("  [StudentService] getCount() ─ exit → {}", count);
        return count;
    }

    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getStudentsPaged(int page, int size, String sortBy, String direction) {
        log.info("  [StudentService] getStudentsPaged(page={}, size={}, sortBy='{}', direction='{}') ─ entry", page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        log.info("    [repo] calling studentRepository.findAll(Pageable{{ page={}, size={}, sort='{}' }})", page, size, sort);

        Page<Student> rawPage = studentRepository.findAll(pageable);
        log.info("    [repo] findAll(Pageable) returned page {}/{}, {} items, {} total records",
                rawPage.getNumber(), rawPage.getTotalPages() - 1, rawPage.getNumberOfElements(), rawPage.getTotalElements());

        Page<StudentResponseDTO> result = rawPage.map(StudentResponseDTO::fromEntity);
        log.info("  [StudentService] getStudentsPaged() ─ exit → page {}, {} items", result.getNumber(), result.getNumberOfElements());
        return result;
    }

    @Transactional
    public List<StudentResponseDTO> enrollBulk(List<StudentRequestDTO> dtos) {
        log.info("  [StudentService] enrollBulk() ─ entry, {} students to enroll", dtos.size());
        List<StudentResponseDTO> saved = new ArrayList<>();

        for (int i = 0; i < dtos.size(); i++) {
            StudentRequestDTO dto = dtos.get(i);
            log.info("  [StudentService] processing student {}/{}: name='{}', email='{}'", i + 1, dtos.size(), dto.getName(), dto.getEmail());

            log.info("    [repo] calling studentRepository.existsByEmail('{}')", dto.getEmail());
            boolean exists = studentRepository.existsByEmail(dto.getEmail());
            log.info("    [repo] existsByEmail('{}') returned {}", dto.getEmail(), exists);

            if (exists) {
                log.warn("  [StudentService] duplicate email '{}' at position {} → ROLLING BACK entire transaction", dto.getEmail(), i + 1);
                throw new DuplicateEmailException(dto.getEmail());
            }

            Student student = new Student(dto.getName(), dto.getEmail(), dto.getCourse());
            log.info("    [repo] calling studentRepository.save('{}')", student.getName());
            Student result = studentRepository.save(student);
            log.info("    [repo] save() returned Student{{ id={} }}", result.getId());

            saved.add(StudentResponseDTO.fromEntity(result));
        }

        log.info("  [StudentService] enrollBulk() ─ exit → {} students committed", saved.size());
        return saved;
    }
}
