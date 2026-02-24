package com.aafo.telemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.aafo.telemetry.repository")
public class DroneTelemetryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DroneTelemetryServiceApplication.class, args);
    }
}
