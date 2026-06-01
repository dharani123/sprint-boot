package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// @RestController = this class handles HTTP requests and returns data (not HTML pages)
@RestController
public class HelloController {

    // Handles: GET http://localhost:8080/hello
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World! Spring Boot is running!";
    }

    // Handles: GET http://localhost:8080/greet/John
    // @PathVariable pulls "John" out of the URL path itself
    @GetMapping("/greet/{name}")
    public String greetByName(@PathVariable String name) {
        return "Hello, " + name + "! Welcome to Spring Boot.";
    }

    // Handles: GET http://localhost:8080/welcome?city=Delhi
    // @RequestParam pulls "Delhi" from the query string (?city=...)
    @GetMapping("/welcome")
    public String welcomeWithCity(@RequestParam(defaultValue = "the world") String city) {
        return "Welcome from " + city + "!";
    }
}
