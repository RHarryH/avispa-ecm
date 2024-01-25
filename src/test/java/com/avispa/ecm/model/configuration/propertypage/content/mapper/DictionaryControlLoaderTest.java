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

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.dictionary.DictionaryNotFoundException;
import com.avispa.ecm.model.configuration.dictionary.DictionaryValue;
import com.avispa.ecm.model.configuration.propertypage.content.control.Combo;
import com.avispa.ecm.model.configuration.propertypage.content.control.dictionary.DictionaryLoad;
import com.avispa.ecm.model.configuration.propertypage.content.control.dictionary.DynamicLoad;
import com.avispa.ecm.model.document.DocumentRepository;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeRepository;
import com.avispa.ecm.util.TestDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
@SpringBootTest
class DictionaryControlLoaderTest {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private EcmConfigRepository<Dictionary> ecmConfigRepository;

    @Autowired
    private DictionaryControlLoader controlLoader;

    @BeforeEach
    void init() {
        typeRepository.save(getType());
    }

    @AfterEach
    void cleanup() {
        typeRepository.deleteAll();
        documentRepository.deleteAll();
        ecmConfigRepository.deleteAll();
    }

    @Test
    void givenControlWithTypeName_whenLoadDictionary_thenTypeObjectsAreLoaded() {
        Combo combo = new Combo();
        combo.setProperty("testDate");
        combo.setLoadSettings(new DynamicLoad("Test document"));

        UUID id = persistTestDocument("Test document");
        persistTestDocument(""); // should be ignored in test

        var result = controlLoader.loadDictionary(combo, new TestDocument());

        assertEquals(Map.of(id.toString(), "Test document"), result);
    }

    @Test
    void givenControlWithTypeNameAndQualification_whenLoadDictionary_thenTypeObjectsAreLoaded() {
        Combo combo = new Combo();
        combo.setProperty("testDate");
        combo.setLoadSettings(new DynamicLoad("Test document", "{\"objectName\": \"Test document\"}"));

        UUID id = persistTestDocument("Test document");
        persistTestDocument("Test document 2");

        var result = controlLoader.loadDictionary(combo, new TestDocument());
        assertEquals(Map.of(id.toString(), "Test document"), result);

        result = controlLoader.loadDynamicDictionary((DynamicLoad) combo.getLoadSettings(), new TestDocument());
        assertEquals(Map.of(id.toString(), "Test document"), result);
    }

    @Test
    void givenControlWithTypeNameAndQualificationWithExpressions_whenLoadDictionary_thenTypeObjectsAreLoaded() {
        Combo combo = new Combo();
        combo.setProperty("testDate");
        combo.setLoadSettings(new DynamicLoad("Test document", "{\"objectName\": { \"$like\": \"$value('objectName')%\"}}"));

        UUID id = persistTestDocument("Test document");
        UUID id2 = persistTestDocument("Test document 2");

        TestDocument testDocument = new TestDocument();
        testDocument.setObjectName("Test");
        var result = controlLoader.loadDictionary(combo, testDocument);

        assertEquals(Map.of(id.toString(), "Test document", id2.toString(), "Test document 2"), result);
    }

    @Test
    void givenControlWithTypeNameAndQualification_whenLoadDynamicDictionary_thenTypeObjectsAreLoaded() {
        DynamicLoad dynamicLoad = new DynamicLoad("Test document", "{\"objectName\": \"Test document\"}");

        UUID id = persistTestDocument("Test document");
        persistTestDocument("Test document 2");

        var result = controlLoader.loadDynamicDictionary(dynamicLoad, new TestDocument());
        assertEquals(Map.of(id.toString(), "Test document"), result);
    }

    @Test
    void givenControlWithDictionary_whenLoadDictionary_thenDictionaryIsLoaded() {
        Combo combo = new Combo();
        combo.setProperty("testInt");
        combo.setLoadSettings(new DictionaryLoad("Test Dictionary"));

        persistTestDictionary();

        var result = controlLoader.loadDictionary(combo, new TestDocument());

        assertEquals(Map.of("Key 1", "Label 1", "Key 3", "Alpha"), result);
    }

    @Test
    void givenControlWithDictionarySortedByLabel_whenLoadDictionary_thenDictionaryIsLoaded() {
        Combo combo = new Combo();
        combo.setProperty("testInt");
        combo.setLoadSettings(new DictionaryLoad("Test Dictionary", true));

        persistTestDictionary();

        var result = controlLoader.loadDictionary(combo, new TestDocument());

        assertEquals(Map.of("Key 3", "Alpha", "Key 1", "Label 1"), result);
    }

    @Test
    void givenControl_whenLoadDictionary_thenDictionaryIsLoadedFromAnnotation() {
        Combo combo = new Combo();
        combo.setProperty("testString");

        persistTestDictionary();

        var result = controlLoader.loadDictionary(combo, new TestDocument());

        assertEquals(Map.of("Key 1", "Label 1", "Key 3", "Alpha"), result);
    }

    @Test
    void givenControlWithEmptyDictionaryName_whenLoadDictionary_thenDictionaryIsLoadedFromAnnotation() {
        Combo combo = new Combo();
        combo.setProperty("testString");
        combo.setLoadSettings(new DictionaryLoad());

        persistTestDictionary();

        var result = controlLoader.loadDictionary(combo, new TestDocument());

        assertEquals(Map.of("Key 1", "Label 1", "Key 3", "Alpha"), result);
    }

    @Test
    void givenControl_whenLoadUnknownDictionary_thenThrowException() {
        Combo combo = new Combo();
        combo.setProperty("testString");

        var context = new TestDocument();
        assertThrows(DictionaryNotFoundException.class, () -> controlLoader.loadDictionary(combo, context));
    }

    @Test
    void givenControlWithoutAnyDictionaryDate_whenLoadDictionary_thenThrowException() {
        Combo combo = new Combo();
        combo.setProperty("testInt");

        var context = new TestDocument();
        assertThrows(DictionaryNotFoundException.class, () -> controlLoader.loadDictionary(combo, context));
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
        return documentRepository.save(testDocument).getId();
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

        ecmConfigRepository.save(dictionary);
    }
}