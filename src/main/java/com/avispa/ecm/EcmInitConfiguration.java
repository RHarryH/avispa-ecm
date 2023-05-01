package com.avispa.ecm;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
/* This annotation has to be outside main config, otherwise 'JPA metamodel must not be empty!' exception
   will be thrown during tests run */
@EnableJpaAuditing
public class EcmInitConfiguration {}