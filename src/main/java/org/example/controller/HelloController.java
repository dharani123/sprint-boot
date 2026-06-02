package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        log.info("→ [HelloController] GET /hello called");
        String result = "Hello, World! Spring Boot is running!";
        log.info("← [HelloController] GET /hello returning: '{}'", result);
        return result;
    }

    @GetMapping("/greet/{name}")
    public String greetByName(@PathVariable String name) {
        log.info("→ [HelloController] GET /greet/{name} called, name='{}'", name);
        String result = "Hello, " + name + "! Welcome to Spring Boot.";
        log.info("← [HelloController] GET /greet/{name} returning: '{}'", result);
        return result;
    }

    @GetMapping("/welcome")
    public String welcomeWithCity(@RequestParam(defaultValue = "the world") String city) {
        log.info("→ [HelloController] GET /welcome called, city='{}'", city);
        String result = "Welcome from " + city + "!";
        log.info("← [HelloController] GET /welcome returning: '{}'", result);
        return result;
    }
}
