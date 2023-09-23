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

package com.avispa.ecm.util.parser;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class CsvParserTest {
    private final CsvParser csvParser = new CsvParser(',');

    @Test
    void givenCsvFile_whenParse_thenParsed() {
        var result = csvParser.parse(getInputStream("parser/test.csv"));

        assertEquals(List.of(
                List.of("1", "2", "3"),
                List.of("4", "5", "6")
        ), result);
    }

    @Test
    void givenCsvFileWithUnsupportedSeparator_whenParse_thenParsed() {
        var result = csvParser.parse(getInputStream("parser/test-unsupported-separator.csv"));

        assertEquals(List.of(
                List.of("1;2;3"),
                List.of("4;5;6")
        ), result);
    }

    @Test
    void givenCsvFileWithQuotes_whenParse_thenParsed() {
        var result = csvParser.parse(getInputStream("parser/test-quotes.csv"));

        assertEquals(List.of(
                List.of("1,2", "3"),
                List.of("4", "5,6")
        ), result);
    }

    @Test
    void givenCsvFileWithUnevenColumns_whenParse_thenParsed() {
        var result = csvParser.parse(getInputStream("parser/test-uneven-columns.csv"));

        assertEquals(List.of(
                List.of("1", "2", "3"),
                List.of("4", "5")
        ), result);
    }

    private InputStream getInputStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }
}