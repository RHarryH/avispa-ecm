package com.avispa.ecm.util.json;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
class JsonValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "content/columns.json",
            "content/combo.json",
            "content/date.json",
            "content/money.json",
            "content/number.json",
            "content/radio.json",
            "content/table.json",
            "content/tabs.json",
            "content/textarea.json"
    })
    void validatePropertyPageSchema(String jsonFilePath) {
        validate(jsonFilePath);
    }

    private void validate(String jsonFilePath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath);
        assertTrue(JsonValidator.validateJson(inputStream, "/json-schemas/property-page-content.json"));
    }

}