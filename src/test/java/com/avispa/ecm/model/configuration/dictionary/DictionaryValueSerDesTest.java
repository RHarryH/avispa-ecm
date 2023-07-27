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

package com.avispa.ecm.model.configuration.dictionary;

import com.avispa.ecm.EcmConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
@Import(EcmConfiguration.class)
@JsonTest
@Slf4j
class DictionaryValueSerDesTest {
    private static Dictionary dictionary;

    private static final String referenceJson = "{\"value\":{\"id\":\"%s\",\"objectName\":\"Key\",\"columns\":{\"A\":\"B\",\"C\":\"D\"},\"label\":\"Label\",\"dictionary\":{\"id\":\"%s\",\"objectName\":\"Test dictionary\",\"values\":[\"%s\"],\"empty\":false}}}";

    @Getter
    @Setter
    static class TestObject {
        private DictionaryValue value;
    }

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void init() {
        dictionary = new Dictionary();
        dictionary.setId(UUID.randomUUID());
        dictionary.setObjectName("Test dictionary");

        DictionaryValue value = new DictionaryValue();
        value.setId(UUID.randomUUID());
        value.setLabel("Label");
        value.setKey("Key");
        value.setColumns(Map.of("A", "B", "C", "D"));
        dictionary.addValue(value);
    }

    @Test
    void givenTestObject_whenConvertToJson_thenDictionaryValueSerialized() throws JsonProcessingException {
        DictionaryValue value = dictionary.getValue("Key");
        JsonNode reference = objectMapper.readTree(String.format(referenceJson, value.getId(), value.getDictionary().getId(), value.getId()));

        TestObject testObject = new TestObject();
        testObject.setValue(value);

        JsonNode converted = objectMapper.valueToTree(testObject);

        assertTrue(converted.hasNonNull("value"));
        assertEquals(reference, converted);
    }

    @Test
    void givenTestJson_whenConvertToObject_thenDictionaryValueDeserialized() {
        DictionaryValue value = dictionary.getValue("Key");

        TestObject ecmObject = null;
        try {
            ecmObject = objectMapper.readValue(String.format(referenceJson, value.getId(), value.getDictionary().getId(), value.getId()), TestObject.class);
        } catch (JsonProcessingException e) {
            log.error("Can't convert JSON to object", e);
        }

        assertNotNull(ecmObject);
        assertEquals(UUID.fromString(value.getId().toString()), ecmObject.getValue().getId());
        assertEquals(value.getKey(), ecmObject.getValue().getKey());
        assertEquals(value.getLabel(), ecmObject.getValue().getLabel());
        assertEquals(value.getColumns(), ecmObject.getValue().getColumns());
        assertEquals(value.getDictionary().getId(), ecmObject.getValue().getDictionary().getId());
    }
}