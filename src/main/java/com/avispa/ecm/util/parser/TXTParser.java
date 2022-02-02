package com.avispa.ecm.util.parser;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class TXTParser implements IFileParser {
    @Override
    public List<List<String>> parse(File file) {
        List<List<String>> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(List.of(line));
            }
        } catch (IOException e) {
            log.error(String.format("Could not parse file: %s", file), e);
        }

        return result;
    }
}
