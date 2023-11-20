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

package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.callable.autolink.Autolink;
import com.avispa.ecm.model.configuration.callable.autoname.Autoname;
import com.avispa.ecm.model.configuration.context.Context;
import com.avispa.ecm.model.configuration.load.dto.ContextDto;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ContextMapperImpl.class})
class ContextMapperTest {

    @Autowired
    private ContextMapper mapper;

    @MockBean
    private EcmConfigRepository<EcmConfig> ecmConfigRepository;

    @MockBean
    private TypeService typeService;

    @Test
    void givenDto_whenConvert_thenCorrectEntity() {
        ContextDto contextDto = getSampleDto();

        Type type = getType();
        when(typeService.getType("Document")).thenReturn(type);

        Autolink autolink = getAutolinkConfig();
        when(ecmConfigRepository.findByObjectName("Autolink")).thenReturn(Optional.of(autolink));

        Autoname autoname = getAutonameConfig();
        when(ecmConfigRepository.findByObjectName("Autoname")).thenReturn(Optional.of(autoname));

        Context convertedEntity = mapper.convertToEntity(contextDto);

        assertAll(() -> {
            assertEquals("Context Dto", convertedEntity.getObjectName());
            assertEquals(type, convertedEntity.getType());
            assertEquals("{}", convertedEntity.getMatchRule());
            assertEquals(2, convertedEntity.getImportance());
            assertEquals(1, convertedEntity.getEcmConfigs().size());
            assertEquals(List.of(autolink), convertedEntity.getEcmConfigs());
        });
    }

    @Test
    void givenDtoAndEntity_whenUpdate_thenEntityHasDtoProperties() {
        Context context = getSampleEntity();
        ContextDto contextDto = getSampleDto();

        Type type = getType();
        when(typeService.getType("Document")).thenReturn(type);

        Autolink autolink = getAutolinkConfig();
        when(ecmConfigRepository.findByObjectName("Autolink")).thenReturn(Optional.of(autolink));

        mapper.updateEntityFromDto(contextDto, context);

        assertAll(() -> {
            assertEquals("Context Dto", context.getObjectName());
            assertEquals(type, context.getType());
            assertEquals("{}", context.getMatchRule());
            assertEquals(2, context.getImportance());
            assertEquals(1, context.getEcmConfigs().size());
            assertEquals(List.of(autolink), context.getEcmConfigs());
        });
    }

    @Test
    void givenDtoAndEmptyEntity_whenUpdate_thenEntityHasDtoProperties() {
        Context context = new Context();
        ContextDto contextDto = getSampleDto();

        Type type = getType();
        when(typeService.getType("Document")).thenReturn(type);

        Autolink autolink = getAutolinkConfig();
        when(ecmConfigRepository.findByObjectName("Autolink")).thenReturn(Optional.of(autolink));

        mapper.updateEntityFromDto(contextDto, context);

        assertAll(() -> {
            assertEquals("Context Dto", context.getObjectName());
            assertEquals(type, context.getType());
            assertEquals("{}", context.getMatchRule());
            assertEquals(2, context.getImportance());
            assertEquals(1, context.getEcmConfigs().size());
            assertEquals(List.of(autolink), context.getEcmConfigs());
        });
    }

    @Test
    void givenNullDto_whenUpdate_thenDoNothing() {
        Context context = getSampleEntity();
        mapper.updateEntityFromDto(null, context);

        Type type = getType();
        when(typeService.getType("Document")).thenReturn(type);

        assertAll(() -> {
            assertEquals("Context entity", context.getObjectName());
            assertEquals(type, context.getType());
            assertEquals("{ \"objectName\": \"Test\"}", context.getMatchRule());
            assertEquals(1, context.getImportance());
            assertEquals(2, context.getEcmConfigs().size());
        });
    }

    private Context getSampleEntity() {
        Context context = new Context();
        context.setObjectName("Context entity");
        context.setType(getType());
        context.setMatchRule("{ \"objectName\": \"Test\"}");
        context.setImportance(1);

        List<EcmConfig> configs = new ArrayList<>();
        configs.add(getAutonameConfig());
        configs.add(getAutolinkConfig());

        context.setEcmConfigs(configs);

        return context;
    }

    private ContextDto getSampleDto() {
        ContextDto contextDto = new ContextDto();
        contextDto.setName("Context Dto");
        contextDto.setType("Document");
        contextDto.setImportance(2);

        List<String> configNames = new ArrayList<>();
        configNames.add("Autolink");

        contextDto.setConfigNames(configNames);

        return contextDto;
    }

    private Autolink getAutolinkConfig() {
        Autolink autolink = new Autolink();
        autolink.setObjectName("Autolink");

        return autolink;
    }

    private Autoname getAutonameConfig() {
        Autoname autoname = new Autoname();
        autoname.setObjectName("Autoname");

        return autoname;
    }

    private Type getType() {
        Type type = new Type();
        type.setObjectName("Document");
        type.setEntityClass(Document.class);

        return type;
    }
}