package com.harbojohnston.qrgeneraterbackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class QrGeneraterBackendApplication {
    private static Logger log = LoggerFactory.getLogger(QrGeneraterBackendApplication.class);

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

}
