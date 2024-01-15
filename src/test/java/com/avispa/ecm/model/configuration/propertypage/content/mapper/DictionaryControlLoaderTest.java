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

package com.avispa.ecm.model.configuration.propertypage.content.mapper;

import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.dictionary.DictionaryNotFoundException;
import com.avispa.ecm.model.configuration.dictionary.DictionaryService;
import com.avispa.ecm.model.configuration.dictionary.DictionaryValue;
import com.avispa.ecm.model.configuration.propertypage.content.control.ComboRadio;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeService;
import com.avispa.ecm.util.TestDocument;
import com.avispa.ecm.util.condition.ConditionParser;
import com.avispa.ecm.util.condition.ConditionRunner;
import com.avispa.ecm.util.condition.ConditionService;
import com.avispa.ecm.util.json.JsonValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
@DataJpaTest
@Import({DictionaryControlLoader.class, DictionaryService.class, TypeService.class,
        ConditionService.class, ConditionParser.class, ConditionRunner.class, ObjectMapper.class, JsonValidator.class})
class DictionaryControlLoaderTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DictionaryControlLoader controlLoader;

    @BeforeEach
    void init() {
        entityManager.persist(getType());
    }

    @AfterEach
    void cleanup() {
        entityManager.clear();
    }

    @Test
    void givenControlWithTypeName_whenLoadDictionary_thenTypeObjectsAreLoaded() {
        ComboRadio comboRadio = new ComboRadio();
        comboRadio.setProperty("testDate");
        comboRadio.setDynamic(ComboRadio.Dynamic.ofTypeName("Test document"));

        UUID id = persistTestDocument("Test document");
        persistTestDocument(""); // should be ignored in test

        controlLoader.loadDictionary(comboRadio, TestDocument.class);

        assertEquals(Map.of(id.toString(), "Test document"), comboRadio.getOptions());
    }

    @Test
    void givenControlWithDictionary_whenLoadDictionary_thenDictionaryIsLoaded() {
        ComboRadio comboRadio = new ComboRadio();
        comboRadio.setProperty("testInt");
        comboRadio.setDictionary(new ComboRadio.Dictionary("Test Dictionary"));

        persistTestDictionary();

        controlLoader.loadDictionary(comboRadio, TestDocument.class);

        assertEquals(Map.of("Key 1", "Label 1", "Key 3", "Alpha"), comboRadio.getOptions());
    }

    @Test
    void givenControlWithDictionarySortedByLabel_whenLoadDictionary_thenDictionaryIsLoaded() {
        ComboRadio comboRadio = new ComboRadio();
        comboRadio.setProperty("testInt");
        comboRadio.setDictionary(new ComboRadio.Dictionary("Test Dictionary", true));

        persistTestDictionary();

        controlLoader.loadDictionary(comboRadio, TestDocument.class);

        assertEquals(Map.of("Key 3", "Alpha", "Key 1", "Label 1"), comboRadio.getOptions());
    }

    @Test
    void givenControl_whenLoadDictionary_thenDictionaryIsLoadedFromAnnotation() {
        ComboRadio comboRadio = new ComboRadio();
        comboRadio.setProperty("testString");

        persistTestDictionary();

        controlLoader.loadDictionary(comboRadio, TestDocument.class);

        assertEquals(Map.of("Key 1", "Label 1", "Key 3", "Alpha"), comboRadio.getOptions());
    }

    @Test
    void givenControlWithEmptyDictionaryName_whenLoadDictionary_thenDictionaryIsLoadedFromAnnotation() {
        ComboRadio comboRadio = new ComboRadio();
        comboRadio.setProperty("testString");
        comboRadio.setDictionary(new ComboRadio.Dictionary());

        persistTestDictionary();

        controlLoader.loadDictionary(comboRadio, TestDocument.class);

        assertEquals(Map.of("Key 1", "Label 1", "Key 3", "Alpha"), comboRadio.getOptions());
    }

    @Test
    void givenControl_whenLoadUnknownDictionary_thenThrowException() {
        ComboRadio comboRadio = new ComboRadio();
        comboRadio.setProperty("testString");

        assertThrows(DictionaryNotFoundException.class, () -> controlLoader.loadDictionary(comboRadio, TestDocument.class));
    }

    @Test
    void givenControlWithoutAnyDictionaryDate_whenLoadDictionary_thenThrowException() {
        ComboRadio comboRadio = new ComboRadio();
        comboRadio.setProperty("testInt");

        assertThrows(DictionaryNotFoundException.class, () -> controlLoader.loadDictionary(comboRadio, TestDocument.class));
    }

    private Type getType() {
        Type type = new Type();
        type.setObjectName("Test document");
        type.setEntityClass(TestDocument.class);

        return type;
    }

    private UUID persistTestDocument(String documentName) {
        TestDocument testDocument = new TestDocument();
        testDocument.setObjectName(documentName);
        return entityManager.persist(testDocument).getId();
    }

    private void persistTestDictionary() {
        Dictionary dictionary = new Dictionary();
        dictionary.setObjectName("Test Dictionary");

        DictionaryValue dv1 = new DictionaryValue();
        dv1.setKey("Key 1");
        dv1.setLabel("Label 1");

        DictionaryValue dv2 = new DictionaryValue(); // should be ignored
        dv2.setKey("Key 2");
        dv2.setLabel("");

        DictionaryValue dv3 = new DictionaryValue(); // should be first when sorting by label
        dv3.setKey("Key 3");
        dv3.setLabel("Alpha");

        dictionary.addValue(dv1);
        dictionary.addValue(dv2);
        dictionary.addValue(dv3);

        entityManager.persist(dictionary);
    }
}