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

package com.avispa.ecm.model.type;

import com.avispa.ecm.util.DiscriminatedTestDocument;
import com.avispa.ecm.util.TestDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
@DataJpaTest
@Import(TypeService.class)
class TypeServiceTest {
    @Autowired
    private TypeService typeService;

    private Type type;

    @BeforeEach
    void init() {
        type = new Type();
        type.setObjectName("Test document");
        type.setEntityClass(TestDocument.class);

        type = typeService.registerType(type);
    }

    @Test
    void givenType_whenGetDiscriminator_thenReturnFieldName() {
        assertAll(() -> {
            assertEquals("type", TypeService.getTypeDiscriminatorFromAnnotation(DiscriminatedTestDocument.class));
            assertEquals("", TypeService.getTypeDiscriminatorFromAnnotation(TestDocument.class));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"test document", "Test document", "test Document", "TeST DoCumenT"})
    void givenTypeName_whenGetType_thenTypeReturnedFromRepo(String typeName) {
        assertEquals(type.getObjectName(), typeService.getType(typeName).getObjectName());
    }

    @Test
    void givenTypeClass_whenGetName_thenNameReturned() {
        assertEquals("test document", typeService.getTypeName(TestDocument.class));
    }
}