package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // Turns this into a Spring Boot app - scans package, auto-configures everything
public class Main {

    public static void main(String[] args) {
        // This single line boots up the entire Spring container + starts Tomcat on port 8080
        SpringApplication.run(Main.class, args);
    }
}