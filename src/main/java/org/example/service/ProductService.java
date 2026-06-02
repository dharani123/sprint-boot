package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ProductService {

    private final List<Product> products = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public ProductService() {
        add("Milk",         65,  "Dairy & Eggs",   "🥛", 50, "1 Litre");
        add("Eggs",         85,  "Dairy & Eggs",   "🥚", 30, "6 pieces");
        add("Butter",       55,  "Dairy & Eggs",   "🧈", 40, "100 g");
        add("Curd",         45,  "Dairy & Eggs",   "🫙", 60, "400 g");
        add("Paneer",       95,  "Dairy & Eggs",   "🧀", 25, "200 g");
        add("Tomato",       30,  "Fruits & Veggies", "🍅", 80, "500 g");
        add("Onion",        35,  "Fruits & Veggies", "🧅", 70, "1 kg");
        add("Potato",       25,  "Fruits & Veggies", "🥔", 90, "1 kg");
        add("Banana",       40,  "Fruits & Veggies", "🍌", 50, "6 pcs");
        add("Apple",       120,  "Fruits & Veggies", "🍎", 35, "4 pcs");
        add("Bread",        45,  "Bakery",   "🍞", 40, "400 g");
        add("Pav",          25,  "Bakery",   "🥖", 50, "8 pcs");
        add("Rusk",         60,  "Bakery",   "🥐", 30, "16 pcs");
        add("Cookies",      55,  "Bakery",   "🍪", 45, "150 g");
        add("Water",        20,  "Beverages", "💧", 100, "1 Litre");
        add("Mango Juice",  35,  "Beverages", "🥭", 60,  "200 ml");
        add("Tata Tea",    120,  "Beverages", "☕", 40,  "250 g");
        add("Orange Juice", 90,  "Beverages", "🧃", 50,  "1 Litre");
        add("Popcorn",      50,  "Snacks", "🍿", 80, "70 g");
        add("Lay's Chips",  30,  "Snacks", "🍟", 100,"26 g");
        add("Parle-G",      10,  "Snacks", "🫘", 150,"100 g");
        add("5-Star",       20,  "Snacks", "🍫", 120,"45 g");
        add("Shampoo",     180,  "Personal Care", "🧴", 30, "200 ml");
        add("Dove Soap",    40,  "Personal Care", "🧼", 60, "100 g");
        add("Colgate",      85,  "Personal Care", "🪥", 50, "200 g");
        log.info("  [ProductService] initialized with {} products in memory", products.size());
    }

    private void add(String name, double price, String category, String emoji, int stock, String unit) {
        products.add(Product.builder()
                .id(idCounter.getAndIncrement())
                .name(name).price(price).category(category)
                .emoji(emoji).stock(stock).unitLabel(unit)
                .build());
    }

    public List<Product> getAllProducts() {
        log.info("  [ProductService] getAllProducts() ─ entry");
        log.info("    [store] reading in-memory list, size={}", products.size());
        log.info("  [ProductService] getAllProducts() ─ exit → {} products", products.size());
        return products;
    }

    public Optional<Product> getProductById(int id) {
        log.info("  [ProductService] getProductById({}) ─ entry", id);
        log.info("    [store] searching in-memory list for id={}", id);
        Optional<Product> result = products.stream().filter(p -> p.getId() == id).findFirst();
        if (result.isPresent()) {
            log.info("    [store] found Product: name='{}', price={}", result.get().getName(), result.get().getPrice());
        } else {
            log.warn("    [store] no product found for id={}", id);
        }
        log.info("  [ProductService] getProductById({}) ─ exit → {}", id, result.isPresent() ? "found" : "empty");
        return result;
    }

    public Product addProduct(Product product) {
        log.info("  [ProductService] addProduct() ─ entry, name='{}', price={}", product.getName(), product.getPrice());
        product.setId(idCounter.getAndIncrement());
        log.info("    [store] assigning id={}, adding to in-memory list", product.getId());
        products.add(product);
        log.info("  [ProductService] addProduct() ─ exit → id={}, list size now {}", product.getId(), products.size());
        return product;
    }

    public Optional<Product> updateProduct(int id, Product updated) {
        log.info("  [ProductService] updateProduct({}) ─ entry, new name='{}', price={}", id, updated.getName(), updated.getPrice());
        log.info("    [store] searching in-memory list for id={}", id);
        Optional<Product> result = getProductById(id).map(existing -> {
            log.info("    [store] found '{}' → updating fields", existing.getName());
            existing.setName(updated.getName());
            existing.setPrice(updated.getPrice());
            log.info("    [store] update complete: name='{}', price={}", existing.getName(), existing.getPrice());
            return existing;
        });
        if (result.isEmpty()) log.warn("  [ProductService] updateProduct({}) ─ exit → not found", id);
        else log.info("  [ProductService] updateProduct({}) ─ exit → updated successfully", id);
        return result;
    }

    public boolean deleteProduct(int id) {
        log.info("  [ProductService] deleteProduct({}) ─ entry", id);
        log.info("    [store] calling removeIf(id == {}) on in-memory list", id);
        boolean deleted = products.removeIf(p -> p.getId() == id);
        log.info("    [store] removeIf() returned {}, list size now {}", deleted, products.size());
        log.info("  [ProductService] deleteProduct({}) ─ exit → {}", id, deleted ? "deleted" : "not found");
        return deleted;
    }
}
