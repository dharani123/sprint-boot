package org.example.service;

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

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        return studentRepository.findById(id)
                .map(StudentResponseDTO::fromEntity)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Transactional
    public StudentResponseDTO addStudent(StudentRequestDTO dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException(dto.getEmail());
        }
        // Convert DTO → entity. The entity's @Id is null here — DB will assign it.
        Student student = new Student(dto.getName(), dto.getEmail(), dto.getCourse());
        return StudentResponseDTO.fromEntity(studentRepository.save(student));
    }

    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO dto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setCourse(dto.getCourse());
        return StudentResponseDTO.fromEntity(studentRepository.save(existing));
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> findByCourse(String course) {
        return studentRepository.findByCourse(course)
                .stream()
                .map(StudentResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> searchByName(String keyword) {
        return studentRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(StudentResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getCount() {
        return studentRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getStudentsPaged(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        // .map() on Page applies the mapping to every item in the page's content
        return studentRepository.findAll(pageable).map(StudentResponseDTO::fromEntity);
    }

    @Transactional
    public List<StudentResponseDTO> enrollBulk(List<StudentRequestDTO> dtos) {
        List<StudentResponseDTO> saved = new ArrayList<>();
        for (StudentRequestDTO dto : dtos) {
            if (studentRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateEmailException(dto.getEmail());
            }
            Student student = new Student(dto.getName(), dto.getEmail(), dto.getCourse());
            saved.add(StudentResponseDTO.fromEntity(studentRepository.save(student)));
        }
        return saved;
    }
}
