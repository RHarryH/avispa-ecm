package com.avispa.ecm;

import com.avispa.ecm.model.configuration.load.ConfigurationRegistry;
import com.avispa.ecm.util.CustomAsyncExceptionHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.format.DateTimeFormatter;

/**
 * @author Rafał Hiszpański
 */
@SpringBootApplication
@EnableAsync
@Slf4j
public class EcmConfiguration implements AsyncConfigurer {
    public static final String DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final LocalDateTimeSerializer LOCAL_DATETIME_SERIALIZER = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    public static final LocalDateTimeDeserializer LOCAL_DATETIME_DESERIALIZER = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT));

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .serializers(LOCAL_DATETIME_SERIALIZER)
                .deserializers(LOCAL_DATETIME_DESERIALIZER)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToEnable()
                .modulesToInstall(Hibernate5Module.class); // disables serialization of lazy collections
    }

    @Bean
    public ConfigurationRegistry configurationRegistry() {
        return new ConfigurationRegistry();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
