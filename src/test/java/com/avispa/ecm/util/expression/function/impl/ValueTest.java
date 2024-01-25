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

package com.avispa.ecm.util.expression.function.impl;

import com.avispa.ecm.util.TestDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class ValueTest {
    private static TestDocument document;
    private static Map<String, Object> documentMap;
    private final Value value = new Value();

    @BeforeAll
    static void init() {
        var dateTime = LocalDateTime.of(2021, 10, 11, 10, 54, 18);
        document = new TestDocument();
        document.setObjectName("ABC");
        document.setTestDateTime(dateTime);
        document.setTestInt(5);

        documentMap = Map.of("objectName", "ABC", "testDateTime", dateTime, "testInt", 5);
    }

    @Test
    void getStringValue() {
        assertAll(() -> {
            assertEquals("ABC", value.resolve(document, new String[]{"objectName"}));
            assertEquals("ABC", value.resolve(documentMap, new String[]{"objectName"}));
        });
    }

    @Test
    void getIntValue() {
        assertAll(() -> {
            assertEquals("5", value.resolve(document, new String[]{"testInt"}));
            assertEquals("5", value.resolve(documentMap, new String[]{"testInt"}));
        });
    }

    @Test
    void getDateValue() {
        assertAll(() -> {
            assertEquals("2021-10-11T10:54:18", value.resolve(document, new String[]{"testDateTime"}));
            assertEquals("2021-10-11T10:54:18", value.resolve(documentMap, new String[]{"testDateTime"}));
        });
    }

    @Test
    void nonExistingValue() {
        assertAll(() -> {
            assertEquals("", value.resolve(document, new String[]{"doesNotExist"}));
            assertEquals("", value.resolve(documentMap, new String[]{"doesNotExist"}));
        });
    }
}