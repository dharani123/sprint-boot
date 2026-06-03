package org.example.repository;

import org.example.model.Order;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Spring generates: SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}
