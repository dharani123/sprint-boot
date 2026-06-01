package org.example.repository;

import org.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// JpaRepository<Student, Long>:
//   Student = the entity this repo manages
//   Long    = the type of the primary key (@Id)
//
// By just extending JpaRepository, Spring auto-generates ALL of these:
//   save(student)          → INSERT or UPDATE
//   findById(id)           → SELECT WHERE id = ?
//   findAll()              → SELECT * FROM student
//   deleteById(id)         → DELETE WHERE id = ?
//   count()                → SELECT COUNT(*)
//   existsById(id)         → SELECT 1 WHERE id = ?
//
// You write ZERO SQL. Spring generates it all at startup.
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Custom query — Spring reads the method name and generates SQL automatically!
    // "findBy" + "Course" → SELECT * FROM student WHERE course = ?
    List<Student> findByCourse(String course);

    // SELECT * FROM student WHERE name LIKE %keyword%
    List<Student> findByNameContainingIgnoreCase(String keyword);

    // SELECT COUNT(*) > 0 FROM student WHERE email = ?  — used in duplicate check & bulk enroll
    boolean existsByEmail(String email);
}
