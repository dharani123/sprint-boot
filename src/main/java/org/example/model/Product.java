package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private int    id;
    private String name;
    private double price;
    private String category;
    private String emoji;
    private int    stock;
    private String unitLabel;
}
