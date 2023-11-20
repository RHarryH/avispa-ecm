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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
class DictionaryTest {
    @Test
    void givenDictionary_whenAddValue_thenItIsAccessible() {
        Dictionary dictionary = new Dictionary();
        dictionary.setObjectName("Dictionary");
        dictionary.setDescription("Description");

        DictionaryValue dictionaryValue = getDictionaryValue();

        dictionary.addValue(dictionaryValue);

        assertFalse(dictionary.isEmpty());
        assertEquals(dictionaryValue, dictionary.getValue("Key 1"));
        assertEquals(dictionary, dictionaryValue.getDictionary());
    }

    @Test
    void givenDictionaryWithValue_whenRemoveValue_thenDictionaryIsEmpty() {
        Dictionary dictionary = new Dictionary();
        dictionary.setObjectName("Dictionary");
        dictionary.setDescription("Description");

        DictionaryValue dictionaryValue = getDictionaryValue();

        dictionary.addValue(dictionaryValue);
        dictionary.removeValue(dictionaryValue);

        assertTrue(dictionary.isEmpty());
        assertNull(dictionary.getValue("Key 1"));
    }

    @Test
    void givenDictionaryWithMultipleValues_whenClearValues_thenDictionaryIsEmpty() {
        Dictionary dictionary = new Dictionary();
        dictionary.setObjectName("Dictionary");
        dictionary.setDescription("Description");

        DictionaryValue dictionaryValue = getDictionaryValue();
        DictionaryValue dictionaryValue2 = getDictionaryValue("Key 2", "Label 2", Map.of());

        dictionary.addValue(dictionaryValue);
        dictionary.addValue(dictionaryValue2);

        dictionary.clearValues();

        assertTrue(dictionary.isEmpty());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Key 1,Label 1",
            "Key 3, UNKNOWN KEY"
    })
    void givenDictionaryWithValue_whenGetLabel_thenLabelIsReturned(String key, String label) {
        Dictionary dictionary = new Dictionary();
        dictionary.setObjectName("Dictionary");
        dictionary.setDescription("Description");

        DictionaryValue dictionaryValue = getDictionaryValue();

        dictionary.addValue(dictionaryValue);

        assertEquals(label, dictionary.getLabel(key));
    }

    @Test
    void givenEmptyDictionary_whenGetLabel_thenReturnNull() {
        Dictionary dictionary = new Dictionary();
        dictionary.setObjectName("Dictionary");
        dictionary.setDescription("Description");

        assertNull(dictionary.getLabel("Key 1"));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Key 1,Column 1,Value 1",
            "Key 1,Column 3,UNKNOWN COLUMN",
            "Key 3,Column 1,UNKNOWN COLUMN"
    })
    void givenDictionaryWithValue_whenGetColumnValue_thenColumnValueIsReturned(String key, String column, String columnValue) {
        Dictionary dictionary = new Dictionary();
        dictionary.setObjectName("Dictionary");
        dictionary.setDescription("Description");

        DictionaryValue dictionaryValue = getDictionaryValue();

        dictionary.addValue(dictionaryValue);

        assertEquals(columnValue, dictionary.getColumnValue(key, column));
    }

    @Test
    void givenEmptyDictionary_whenGetColumnValue_thenReturnNull() {
        Dictionary dictionary = new Dictionary();
        dictionary.setObjectName("Dictionary");
        dictionary.setDescription("Description");

        assertNull(dictionary.getColumnValue("Key 1", "Column 1"));
    }

    private static DictionaryValue getDictionaryValue() {
        return getDictionaryValue("Key 1", "Label 1", Map.of("Column 1", "Value 1", "Column 2", "Value 2"));
    }

    private static DictionaryValue getDictionaryValue(String key, String label, Map<String, String> columns) {
        Map<String, String> mutableColumns = new HashMap<>(columns);

        DictionaryValue dictionaryValue = new DictionaryValue();
        dictionaryValue.setKey(key);
        dictionaryValue.setLabel(label);
        dictionaryValue.setColumns(mutableColumns);

        return dictionaryValue;
    }
}