package com.avispa.ecm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Slf4j
@Import(EcmConfiguration.class)
public class EcmApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcmApplication.class, args);
    }
}