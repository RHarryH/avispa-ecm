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
