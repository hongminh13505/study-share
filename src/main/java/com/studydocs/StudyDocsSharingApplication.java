package com.studydocs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudyDocsSharingApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyDocsSharingApplication.class, args);
    }
}

