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

import com.avispa.ecm.model.configuration.display.DisplayService;
import com.avispa.ecm.model.configuration.propertypage.content.control.Combo;
import com.avispa.ecm.model.configuration.propertypage.content.control.Label;
import com.avispa.ecm.model.configuration.propertypage.content.control.Text;
import com.avispa.ecm.model.configuration.propertypage.content.control.dictionary.DynamicLoad;
import com.avispa.ecm.util.NestedObject;
import com.avispa.ecm.util.TestDocument;
import com.avispa.ecm.util.expression.ExpressionResolver;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Rafał Hiszpański
 */
class SimpleControlMapperTest {
    private static ExpressionResolver expressionResolver;
    private static DictionaryControlLoader dictionaryControlLoader;

    private static SimpleControlMapper simpleControlMapper;

    @BeforeAll
    static void init() {
        expressionResolver = mock(ExpressionResolver.class);
        dictionaryControlLoader = mock(DictionaryControlLoader.class);
        simpleControlMapper = new SimpleControlMapper(dictionaryControlLoader, expressionResolver, new DisplayService());
    }

    @Test
    @SneakyThrows
    void givenLabelWithCorrectExpression_whenProcess_thenExpressionWasResolved() {
        Label label = new Label();
        label.setExpression("$value('testString')");

        TestDocument testDocument = new TestDocument();

        simpleControlMapper.processControl(label, List.of(), testDocument);

        verify(expressionResolver).resolve(testDocument, "$value('testString')");
    }

    @Test
    void givenPropertyWithoutLabel_whenProcess_thenLabelTakenFromAnnotation() {
        Text text = new Text();
        text.setProperty("testInt");

        TestDocument testDocument = new TestDocument();
        testDocument.setObjectName("Test document");
        testDocument.setTestInt(12);

        simpleControlMapper.processControl(text, List.of(), testDocument);

        assertEquals("Some test integer", text.getLabel());
        assertEquals("12", text.getValue());
    }

    @Test
    void givenPropertyFromBlacklist_whenProcess_thenValueNotFilled() {
        Text text = new Text();
        text.setProperty("testInt");

        TestDocument testDocument = new TestDocument();
        testDocument.setTestInt(12);

        simpleControlMapper.processControl(text, List.of("testInt"), testDocument);

        assertEquals("", text.getValue());
    }

    @Test
    void givenUnknownProperty_whenProcess_thenValueIsEmptyString() {
        Text text = new Text();
        text.setLabel("Label");
        text.setProperty("nonExisting");

        simpleControlMapper.processControl(text, List.of(), new TestDocument());

        assertEquals("", text.getValue());
    }

    @Test
    void givenComboWithoutType_whenProcess_thenDictionaryLoadAttempt() {
        Combo combo = new Combo();
        combo.setLabel("Label");
        combo.setProperty("testString");

        var context = new TestDocument();
        simpleControlMapper.processControl(combo, List.of(), context);

        verify(dictionaryControlLoader).loadDictionary(combo, context);
    }

    @Test
    @SneakyThrows
    void givenComboWithTypeExpression_whenProcess_thenTypeDeductionAttempt() {
        Combo combo = new Combo();
        combo.setLabel("Label");
        combo.setProperty("testString");

        combo.setLoadSettings(new DynamicLoad("$value('typeName')"));

        TestDocument testDocument = new TestDocument();
        simpleControlMapper.processControl(combo, List.of(), testDocument);

        verify(expressionResolver).resolve(testDocument, "$value('typeName')");
    }

    @Test
    void givenControlWithObjectAsProperty_whenProcess_thenReturnMap() {
        Text text = new Text();
        text.setProperty("nestedObject");

        TestDocument testDocument = new TestDocument();

        UUID id = UUID.randomUUID();
        NestedObject nestedObject = new NestedObject();
        nestedObject.setId(id);
        nestedObject.setObjectName("Nested");

        testDocument.setNestedObject(nestedObject);

        simpleControlMapper.processControl(text, List.of(), testDocument);

        assertEquals(Map.of("objectName", "Nested", "id", id.toString()), text.getValue());
    }

    @Test
    void givenControlWithNullObjectAsProperty_whenProcess_thenReturnMap() {
        Text text = new Text();
        text.setProperty("nestedObject");

        simpleControlMapper.processControl(text, List.of(), new TestDocument());

        assertEquals("", text.getValue());
    }

    @Test
    void givenNestedPropertyOnBlacklist_whenProcess_thenValueNotFilled() {
        Text text = new Text();
        text.setProperty("nestedObject.objectName");

        TestDocument testDocument = new TestDocument();
        testDocument.setObjectName("Test document");

        NestedObject nestedObject = new NestedObject();
        nestedObject.setObjectName("Nested");

        testDocument.setNestedObject(nestedObject);

        simpleControlMapper.processControl(text, List.of("objectName"), testDocument);

        assertEquals("", text.getValue());
    }
}