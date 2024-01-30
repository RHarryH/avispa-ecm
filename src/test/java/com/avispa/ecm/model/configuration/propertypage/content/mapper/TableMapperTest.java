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

import com.avispa.ecm.model.configuration.propertypage.content.control.Combo;
import com.avispa.ecm.model.configuration.propertypage.content.control.PropertyControl;
import com.avispa.ecm.model.configuration.propertypage.content.control.Table;
import com.avispa.ecm.model.configuration.propertypage.content.control.Text;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.util.TestDocument;
import com.avispa.ecm.util.exception.EcmException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Rafał Hiszpański
 */
class TableMapperTest {
    private static DictionaryControlLoader dictionaryControlLoader;
    private static TableMapper tableMapper;

    @BeforeAll
    static void init() {
        dictionaryControlLoader = mock(DictionaryControlLoader.class);
        tableMapper = new TableMapper(dictionaryControlLoader);
    }

    @ParameterizedTest
    @ValueSource(strings = {"nonExisting", "nonTable"})
    void givenTableForNonExistingProperty_whenProcessControl_thenExceptionIsThrown(String propertyName) {
        Table table = new Table();
        table.setProperty(propertyName);

        TestDocument testDocument = new TestDocument();
        List<String> emptyList = List.of();
        assertThrows(EcmException.class, () -> tableMapper.processControl(table, emptyList, testDocument));
    }

    @Test
    void givenComboPropertyInTable_whenProcessControl_thenDictionaryLoadAttempt() {
        Table table = new Table();
        table.setProperty("table");

        List<PropertyControl> controls = new ArrayList<>();
        Combo combo = new Combo();
        combo.setProperty("objectName");
        controls.add(combo);
        table.setControls(controls);

        TestDocument testDocument = new TestDocument();
        Document document = new Document();
        document.setObjectName("Table document");
        testDocument.setTable(List.of(document));

        tableMapper.processControl(table, List.of(), testDocument);

        verify(dictionaryControlLoader).loadDictionary(any(Combo.class), eq(Document.class));
    }

    @Test
    void givenDocumentWithNullList_whenProcessControl_thenTableSizeIsZero() {
        Table table = new Table();
        table.setProperty("table");

        List<PropertyControl> controls = new ArrayList<>();
        Text text = new Text();
        text.setProperty("objectName");
        controls.add(text);
        table.setControls(controls);

        tableMapper.processControl(table, List.of(), new TestDocument());

        assertEquals(0, table.getSize());
    }

    @Test
    void givenPropertyInTable_whenProcessControl_thenTableIsCorrectlyProcessed() {
        Table table = new Table();
        table.setProperty("table");

        List<PropertyControl> controls = new ArrayList<>();
        Text text = new Text();
        text.setProperty("objectName");
        controls.add(text);
        table.setControls(controls);

        TestDocument testDocument = new TestDocument();
        Document document = new Document();
        document.setObjectName("Table document");
        testDocument.setTable(List.of(document));

        tableMapper.processControl(table, List.of(), testDocument);

        assertAll(() -> {
            assertEquals(1, table.getSize());
            assertEquals(2, table.getControls().size());

            PropertyControl control = table.getControls().get(0);
            assertTrue(control.isRequired());
            assertEquals(List.of("Table document"), control.getValue());
        });
    }

    @Test
    void givenNonExistingPropertyInTable_whenProcessControl_thenEmptyValueIsPopulated() {
        Table table = new Table();
        table.setProperty("table");

        List<PropertyControl> controls = new ArrayList<>();
        Text text = new Text();
        text.setProperty("nonExisting");
        controls.add(text);
        table.setControls(controls);

        TestDocument testDocument = new TestDocument();
        testDocument.setTable(List.of(new Document()));

        tableMapper.processControl(table, List.of(), testDocument);

        PropertyControl control = table.getControls().get(0);
        assertEquals(List.of(""), control.getValue());
    }

    @Test
    void givenPropertyFromBlackListInTable_whenProcessControl_thenValueNotFilled() {
        Table table = new Table();
        table.setProperty("table");

        List<PropertyControl> controls = new ArrayList<>();
        Text text = new Text();
        text.setProperty("objectName");
        controls.add(text);
        table.setControls(controls);

        TestDocument testDocument = new TestDocument();
        Document document = new Document();
        document.setObjectName("Table document");

        Document document2 = new Document();
        document2.setObjectName("Table document 2");
        testDocument.setTable(List.of(document, document2));

        tableMapper.processControl(table, List.of("objectName"), testDocument);

        assertAll(() -> {
            assertEquals(2, table.getSize());
            assertEquals(2, table.getControls().size());

            PropertyControl control = table.getControls().get(0);
            assertTrue(control.isRequired());
            assertEquals(List.of("", ""), control.getValue());
        });
    }
}