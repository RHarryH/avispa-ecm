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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class DateValueTest {
    private final DateValue dateValue = new DateValue();

    private static TestDocument document;
    private static Map<String, Object> documentMap;

    @BeforeAll
    static void init() {
        var date = LocalDate.of(2020, 9, 5);
        var dateTime = LocalDateTime.of(2021, 10, 11, 10, 54, 18);
        document = new TestDocument();
        document.setTestDate(date);
        document.setTestDateTime(dateTime);

        documentMap = Map.of("testDate", date, "testDateTime", dateTime);
    }

    @Test
    void noPattern() {
        assertAll(() -> {
            assertEquals("", dateValue.resolve(document, new String[]{"testDateTime", ""}));
            assertEquals("", dateValue.resolve(documentMap, new String[]{"testDateTime", ""}));
        });
    }

    @Test
    void simplePattern() {
        assertAll(() -> {
            assertEquals("10", dateValue.resolve(document, new String[]{"testDateTime", "MM"}));
            assertEquals("10", dateValue.resolve(documentMap, new String[]{"testDateTime", "MM"}));
        });
    }

    @Test
    void complexPattern() {
        assertAll(() -> {
            assertEquals("2021-10-11 10:54:18", dateValue.resolve(document, new String[]{"testDateTime", "yyyy-MM-dd HH:mm:ss"}));
            assertEquals("2021-10-11 10:54:18", dateValue.resolve(documentMap, new String[]{"testDateTime", "yyyy-MM-dd HH:mm:ss"}));
        });
    }

    @Test
    void localDateOnly() {
        assertAll(() -> {
            assertEquals("2020/09", dateValue.resolve(document, new String[]{"testDate", "yyyy/MM"}));
            assertEquals("2020/09", dateValue.resolve(documentMap, new String[]{"testDate", "yyyy/MM"}));
        });
    }
}