/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2023 Rafał Hiszpański
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.avispa.ecm;

import com.avispa.ecm.model.configuration.load.ConfigurationRegistry;
import com.avispa.ecm.util.CustomAsyncExceptionHandler;
import com.avispa.ecm.util.Version;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.format.DateTimeFormatter;

import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

/**
 * @author Rafał Hiszpański
 */
@Configuration
@EnableAsync
@Slf4j
public class EcmConfiguration implements AsyncConfigurer {
    public static final String DATETIME_FORMAT = "dd MMM yyyy, hh:mm:ss";

    public static final LocalDateTimeSerializer LOCAL_DATETIME_SERIALIZER = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    public static final LocalDateTimeDeserializer LOCAL_DATETIME_DESERIALIZER = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT));

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .serializers(LOCAL_DATETIME_SERIALIZER)
                .deserializers(LOCAL_DATETIME_DESERIALIZER)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToEnable(ACCEPT_CASE_INSENSITIVE_ENUMS)
                .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)
                .modulesToInstall(Hibernate6Module.class); // disables serialization of lazy collections
    }

    @Bean
    public ConfigurationRegistry configurationRegistry() {
        return new ConfigurationRegistry();
    }

    @Bean
    public Version ecmVersion(@Value("${avispa.ecm.name}") String applicationName, @Value("${avispa.ecm.version}") String number) {
        return new Version(applicationName, number);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
