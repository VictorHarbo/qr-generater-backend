package com.harbojohnston.qrgeneraterbackend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class QrGeneraterBackendApplication {
    private static final Logger log = LoggerFactory.getLogger(QrGeneraterBackendApplication.class);

    @Autowired
    private YAMLConfig config;

    public static void main(String[] args) {
        SpringApplication.run(QrGeneraterBackendApplication.class, args);
    }



    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "name", defaultValue = "World" ) String name) {
        log.info("Hello endpoint called.");
        return String.format("Hello %s!", name);
    }

    @GetMapping("/ping")
    public String ping() {
        log.info("Ping endpoint called.");
        return "pong";
    }

    @GetMapping("/status")
    public String status() {
        log.info("Status endpoint called.");
        log.debug("The following values are to be delivered through the endpoint: Name: '{}', Environment: '{}'", config.getName(), config.getEnvironment());

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("Application", config.getName());
        jsonData.put("Environment", config.getEnvironment());

        try {
            return objectMapper.writeValueAsString(jsonData);

        } catch (JsonProcessingException e) {
            log.error("An error occurred while processing JSON: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }


    }

}
