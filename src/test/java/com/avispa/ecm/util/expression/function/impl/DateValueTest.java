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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class DateValueTest {
    private final DateValue dateValue = new DateValue();

    private static TestDocument document;

    @BeforeAll
    static void init() {
        document = new TestDocument();
        document.setTestDate(LocalDate.of(2020, 9, 5));
        document.setTestDateTime(LocalDateTime.of(2021, 10, 11, 10, 54, 18));
    }

    @Test
    void noPattern() {
        assertEquals("", dateValue.resolve(document, new String[]{"testDateTime", ""}));
    }

    @Test
    void simplePattern() {
        assertEquals("10", dateValue.resolve(document, new String[]{"testDateTime", "MM"}));
    }

    @Test
    void complexPattern() {
        assertEquals("2021-10-11 10:54:18", dateValue.resolve(document, new String[]{"testDateTime", "yyyy-MM-dd HH:mm:ss"}));
    }

    @Test
    void localDateOnly() {
        assertEquals("2020/09", dateValue.resolve(document, new String[]{"testDate", "yyyy/MM"}));
    }
}