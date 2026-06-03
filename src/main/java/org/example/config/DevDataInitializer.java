package org.example.config;

import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        seedProducts();
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            log.info(">>> Products already seeded, skipping.");
            return;
        }
        save("Milk",         65.0,  "Dairy & Eggs",     "🥛", 50,  "1 Litre");
        save("Eggs",         85.0,  "Dairy & Eggs",     "🥚", 30,  "6 pieces");
        save("Butter",       55.0,  "Dairy & Eggs",     "🧈", 40,  "100 g");
        save("Curd",         45.0,  "Dairy & Eggs",     "🫙", 60,  "400 g");
        save("Paneer",       95.0,  "Dairy & Eggs",     "🧀", 25,  "200 g");
        save("Tomato",       30.0,  "Fruits & Veggies", "🍅", 80,  "500 g");
        save("Onion",        35.0,  "Fruits & Veggies", "🧅", 70,  "1 kg");
        save("Potato",       25.0,  "Fruits & Veggies", "🥔", 90,  "1 kg");
        save("Banana",       40.0,  "Fruits & Veggies", "🍌", 50,  "6 pcs");
        save("Apple",       120.0,  "Fruits & Veggies", "🍎", 35,  "4 pcs");
        save("Bread",        45.0,  "Bakery",           "🍞", 40,  "400 g");
        save("Pav",          25.0,  "Bakery",           "🥖", 50,  "8 pcs");
        save("Rusk",         60.0,  "Bakery",           "🥐", 30,  "16 pcs");
        save("Cookies",      55.0,  "Bakery",           "🍪", 45,  "150 g");
        save("Water",        20.0,  "Beverages",        "💧", 100, "1 Litre");
        save("Mango Juice",  35.0,  "Beverages",        "🥭", 60,  "200 ml");
        save("Tata Tea",    120.0,  "Beverages",        "☕", 40,  "250 g");
        save("Orange Juice", 90.0,  "Beverages",        "🧃", 50,  "1 Litre");
        save("Popcorn",      50.0,  "Snacks",           "🍿", 80,  "70 g");
        save("Lay's Chips",  30.0,  "Snacks",           "🍟", 100, "26 g");
        save("Parle-G",      10.0,  "Snacks",           "🫘", 150, "100 g");
        save("5-Star",       20.0,  "Snacks",           "🍫", 120, "45 g");
        save("Shampoo",     180.0,  "Personal Care",    "🧴", 30,  "200 ml");
        save("Dove Soap",    40.0,  "Personal Care",    "🧼", 60,  "100 g");
        save("Colgate",      85.0,  "Personal Care",    "🪥", 50,  "200 g");
        log.info(">>> Seeded {} products.", productRepository.count());
    }

    private void save(String name, Double price, String category, String emoji, Integer stock, String unitLabel) {
        productRepository.save(Product.builder()
                .name(name).price(price).category(category)
                .emoji(emoji).stock(stock).unitLabel(unitLabel)
                .build());
    }
}
