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

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.util.TestDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(MockitoExtension.class)
class DictionaryServiceTest {
    @Mock
    private EcmConfigRepository<Dictionary> dictionaryRepository;

    @InjectMocks
    private DictionaryService dictionaryService;

    @BeforeEach
    void init() {
        lenient().when(dictionaryRepository.findByObjectName(anyString())).thenAnswer(invocation -> {
            if(invocation.getArgument(0).equals("TestDict")) {
                return Optional.of(getTestDictionary());
            }

            throw new DictionaryNotFoundException("TestDict");
        });
    }

    private Dictionary getTestDictionary() {
        Dictionary testDict = new Dictionary();
        testDict.setObjectName("TestDict");

        DictionaryValue dv1 = new DictionaryValue();
        dv1.setKey("first");
        dv1.setLabel("a");

        DictionaryValue dv2 = new DictionaryValue();
        dv2.setKey("second");
        dv2.setLabel("b");

        DictionaryValue dv3 = new DictionaryValue();
        dv3.setKey("third");
        dv3.setLabel("c");

        testDict.addValue(dv1);
        testDict.addValue(dv2);
        testDict.addValue(dv3);

        return testDict;
    }

    @Test
    void givenDictionaryName_whenGetDictionary_thenReturnDictionary() {
        Dictionary dictionary = dictionaryService.getDictionary("TestDict");

        assertEquals("TestDict", dictionary.getObjectName());
    }

    @Test
    void givenIncorrectDictionaryName_whenGetDictionary_thenThrowException() {
        assertThrows(DictionaryNotFoundException.class, () ->
            dictionaryService.getDictionary("Wrong dictionary name"));
    }

    @Test
    void givenEntityClassAndPropertyName_whenGetDictionaryName_thenReturnDictionary() {
        assertEquals("TestDict", dictionaryService.getDictionaryNameFromAnnotation(TestDocument.class, "testString"));
    }

    @Test
    void givenEntityClassAndPropertyName_whenGetDictionary_thenReturnDictionary() {
        Dictionary dictionary = dictionaryService.getDictionary(TestDocument.class, "testString");

        assertEquals("TestDict", dictionary.getObjectName());
    }

    @Test
    void givenEntityClassAndPropertyName_whenGetDictionary_thenThrowException() {
        assertThrows(DictionaryNotFoundException.class, () ->
                dictionaryService.getDictionary(TestDocument.class, "testDateTime"));
    }

    @Test
    void givenEntityClassAndPropertyNameAndPropertyValue_whenGetValueFromDictionary_thenReturnValue() {
        String value = dictionaryService.getValueFromDictionary(TestDocument.class, "testString", "second");

        assertEquals("b", value);
    }

    @Test
    void givenEntityClassAndPropertyNameAndPropertyValue_whenGetValueFromDictionary_thenReturnOriginalValue() {
        String value = dictionaryService.getValueFromDictionary(TestDocument.class, "testString", "fourth");

        assertEquals("fourth", value);
    }

    @Test
    void givenEntityClassAndNestedPropertyNameAndPropertyValue_whenGetValueFromDictionary_thenReturnValue() {
        String value = dictionaryService.getValueFromDictionary(TestDocument.class, "nestedObject.nestedField", "second");

        assertEquals("b", value);
    }
}