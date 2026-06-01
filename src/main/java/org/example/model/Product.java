package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Data = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
// One annotation replaces ~30 lines of boilerplate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder            // generates a builder pattern: Product.builder().name("Laptop").price(75000).build()
public class Product {

    private int id;
    private String name;
    private double price;

    // Zero boilerplate. @Data handles everything.
    // Jackson uses the Lombok-generated getters/setters to serialize/deserialize JSON.
}
