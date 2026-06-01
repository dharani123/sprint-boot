package org.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/info")
public class InfoController {

    // @Value injects a value directly from application.properties
    // ${spring.application.name} reads the key spring.application.name
    @Value("${spring.application.name}")
    private String appName;

    // Environment gives us access to active profiles and any property
    private final Environment environment;

    public InfoController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping
    public Map<String, Object> getInfo() {
        return Map.of(
            "application",     appName,
            "activeProfiles",  environment.getActiveProfiles(),
            "port",            environment.getProperty("server.port"),
            "showSql",         environment.getProperty("spring.jpa.show-sql"),
            "h2ConsoleEnabled",environment.getProperty("spring.h2.console.enabled"),
            "ddlAuto",         environment.getProperty("spring.jpa.hibernate.ddl-auto")
        );
    }
}
