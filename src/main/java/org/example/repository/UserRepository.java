package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Spring reads the method names and generates the SQL automatically:
//   findByEmail("x")  →  SELECT * FROM users WHERE email = 'x'
//   existsByEmail("x") →  SELECT COUNT(*) > 0 FROM users WHERE email = 'x'
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
}
