package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne: many OrderItems belong to one Order
    // @JoinColumn: this is the foreign key column (order_id) in the order_items table
    // @JsonIgnore: prevents infinite recursion when Jackson serializes Order → items → order → items → ...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    private Long   productId;
    private String productName;
    private String emoji;
    private int    quantity;
    private double unitPrice;
    private double itemTotal;
}
