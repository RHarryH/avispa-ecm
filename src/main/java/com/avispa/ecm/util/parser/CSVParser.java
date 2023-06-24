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

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class CSVParser implements IFileParser {
    private char attributeSeparator;

    public CSVParser(char attributeSeparator) {
        this.attributeSeparator = attributeSeparator;
    }

    @Override
    public List<List<String>> parse(File file) {
        List<List<String>> result = new ArrayList<>();
        RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().withSeparator(attributeSeparator).build();
        try (Reader reader = new BufferedReader(new FileReader(file));
             CSVReader cvsReader = new CSVReaderBuilder(reader)
                     .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                     .withCSVParser(rfc4180Parser)
                     .build()) {
            String[] tokens;
            while ((tokens = cvsReader.readNext()) != null) {
                result.add(Arrays.asList(tokens));
            }
        } catch (IOException e) {
            log.error("Could not parse file: {}", file, e);
        } catch (CsvValidationException e) {
            log.error("CSV validation failed for file {}", file, e);
        }

        return result;
    }
}
