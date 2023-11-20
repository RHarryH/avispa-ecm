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

package com.avispa.ecm.model.configuration.display;

import com.avispa.ecm.util.TestDocument;
import com.avispa.ecm.util.exception.EcmException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class DisplayServiceTest {
    private static final DisplayService displayService = new DisplayService();

    @Test
    void whenGetNameOnPropertyWithDisplayAnnotation_thenNameIsReturned() {
        assertEquals("Some test integer", displayService.getDisplayValueFromAnnotation(TestDocument.class, "testInt"));
    }

    @Test
    void whenGetNameOnPropertyWithoutDisplayAnnotation_thenPropertyNameIsReturned() {
        assertEquals("testString", displayService.getDisplayValueFromAnnotation(TestDocument.class, "testString"));
    }

    @Test
    void whenGetNameOnNestedPropertyWithDisplayAnnotation_thenNameIsReturned() {
        assertEquals("Nested field", displayService.getDisplayValueFromAnnotation(TestDocument.class, "nestedObject.nestedField"));
    }

    @Test
    void whenGetNameOnNonExistingProperty_thenExceptionIsThrown() {
        assertThrows(EcmException.class, () -> displayService.getDisplayValueFromAnnotation(TestDocument.class, "nonExisting"));
    }

    @Test
    void whenGetNameOnNonExistingNestedProperty_thenExceptionIsThrown() {
        assertThrows(EcmException.class, () -> displayService.getDisplayValueFromAnnotation(TestDocument.class, "nestedObject.nonExisting"));
    }
}