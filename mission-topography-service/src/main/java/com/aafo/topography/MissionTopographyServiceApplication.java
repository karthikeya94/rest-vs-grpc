package com.aafo.topography;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.aafo.topography.repository")
public class MissionTopographyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MissionTopographyServiceApplication.class, args);
    }
}
