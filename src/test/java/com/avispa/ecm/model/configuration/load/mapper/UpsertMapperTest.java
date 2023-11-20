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

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.load.dto.UpsertDto;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.configuration.upsert.Upsert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UpsertMapperImpl.class})
class UpsertMapperTest {

    @Autowired
    private UpsertMapper mapper;

    @MockBean
    private EcmConfigRepository<PropertyPage> propertyPageRepository;

    @Test
    void givenDto_whenConvert_thenCorrectEntity() {
        UpsertDto upsertDto = getSampleDto();

        PropertyPage propertyPage = getPropertyPage("Property page for Dto");
        when(propertyPageRepository.findByObjectName("Property page for Dto")).thenReturn(Optional.of(propertyPage));

        Upsert convertedEntity = mapper.convertToEntity(upsertDto);

        assertAll(() -> {
            assertEquals("Upsert Dto", convertedEntity.getObjectName());
            assertNotNull(convertedEntity.getPropertyPage());
            assertEquals("Property page for Dto", convertedEntity.getPropertyPage().getObjectName());
        });
    }

    @Test
    void givenDtoAndEntity_whenUpdate_thenEntityHasDtoProperties() {
        Upsert upsert = getSampleEntity();
        UpsertDto upsertDto = getSampleDto();

        PropertyPage propertyPage = getPropertyPage("Property page for Dto");
        when(propertyPageRepository.findByObjectName("Property page for Dto")).thenReturn(Optional.of(propertyPage));

        mapper.updateEntityFromDto(upsertDto, upsert);

        assertAll(() -> {
            assertEquals("Upsert Dto", upsert.getObjectName());
            assertNotNull(upsert.getPropertyPage());
            assertEquals("Property page for Dto", upsert.getPropertyPage().getObjectName());
        });
    }

    @Test
    void givenDtoAndEmptyEntity_whenUpdate_thenEntityHasDtoProperties() {
        Upsert upsert = new Upsert();
        UpsertDto upsertDto = getSampleDto();

        PropertyPage propertyPage = getPropertyPage("Property page for Dto");
        when(propertyPageRepository.findByObjectName("Property page for Dto")).thenReturn(Optional.of(propertyPage));

        mapper.updateEntityFromDto(upsertDto, upsert);

        assertAll(() -> {
            assertEquals("Upsert Dto", upsert.getObjectName());
            assertNotNull(upsert.getPropertyPage());
            assertEquals("Property page for Dto", upsert.getPropertyPage().getObjectName());
        });
    }

    @Test
    void givenNullDto_whenUpdate_thenDoNothing() {
        Upsert upsert = getSampleEntity();
        mapper.updateEntityFromDto(null, upsert);

        assertAll(() -> {
            assertEquals("Upsert entity", upsert.getObjectName());
            assertNotNull(upsert.getPropertyPage());
            assertEquals("Property page", upsert.getPropertyPage().getObjectName());
        });
    }

    private Upsert getSampleEntity() {
        Upsert upsert = new Upsert();
        upsert.setObjectName("Upsert entity");
        upsert.setPropertyPage(getPropertyPage("Property page"));

        return upsert;
    }

    private UpsertDto getSampleDto() {
        UpsertDto upsertDto = new UpsertDto();
        upsertDto.setName("Upsert Dto");
        upsertDto.setPropertyPage("Property page for Dto");

        return upsertDto;
    }

    private PropertyPage getPropertyPage(String objectName) {
        PropertyPage propertyPage = new PropertyPage();
        propertyPage.setObjectName(objectName);

        return propertyPage;
    }
}