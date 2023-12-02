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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class ParserFactoryTest {
    private final ParserFactory parserFactory = new ParserFactory();

    @Test
    void givenParserFactory_whenCsvExt_thenReturnCsvParser() {
        assertInstanceOf(CsvParser.class, parserFactory.get("csv"));
    }

    @Test
    void givenParserFactory_whenTxtExt_thenReturnCsvParser() {
        assertInstanceOf(TxtParser.class, parserFactory.get("txt"));
    }

    @Test
    void givenParserFactory_whenUnknownExt_thenReturnCsvParser() {
        assertThrows(IllegalArgumentException.class, () -> parserFactory.get("unk"));
    }
}