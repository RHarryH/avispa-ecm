/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2024 Rafał Hiszpański
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

package com.avispa.ecm.util.reflect;

import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.util.NestedObject;
import com.avispa.ecm.util.TestDocument;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Rafał Hiszpański
 */
class EcmPropertyUtilsTest {

    @Test
    void nonStringFieldTest() {
        TestDocument testDocument = new TestDocument();
        testDocument.setTestInt(5);

        assertEquals(5, EcmPropertyUtils.getProperty(testDocument, "testInt"));
    }

    @Test
    void nonExistingFieldTest() {
        TestDocument testDocument = new TestDocument();

        assertNull(EcmPropertyUtils.getProperty(testDocument, "nonExisting"));
    }

    @Test
    void nestedObjectTest() {
        TestDocument testDocument = new TestDocument();

        NestedObject nestedObject = new NestedObject();
        nestedObject.setNestedField("AAA");

        testDocument.setNestedObject(nestedObject);

        assertEquals("AAA", EcmPropertyUtils.getProperty(testDocument, "nestedObject.nestedField"));

        EcmPropertyUtils.setProperty(testDocument, "nestedObject.nestedField", "XYZ");

        assertEquals("XYZ", EcmPropertyUtils.getProperty(testDocument, "nestedObject.nestedField"));
    }

    @Test
    void tableTest() {
        TestDocument testDocument = new TestDocument();

        Document document = new Document();
        document.setObjectName("ABC");

        Document document1 = new Document();
        document1.setObjectName("DEF");

        testDocument.setTable(List.of(document, document1));

        EcmPropertyUtils.setProperty(testDocument, "table[0].objectName", "XYZ");

        assertEquals("XYZ", EcmPropertyUtils.getProperty(testDocument, "table[0].objectName"));
        assertEquals("DEF", EcmPropertyUtils.getProperty(testDocument, "table[1].objectName"));
    }

    @Test
    void describeTest() {
        TestDocument testDocument = new TestDocument();
        testDocument.setTestInt(5);

        NestedObject nestedObject = new NestedObject();
        nestedObject.setNestedField("AAA");

        testDocument.setNestedObject(nestedObject);

        Map<String, Object> expected = new HashMap<>();
        expected.put("pdfRenditionAvailable", false);
        expected.put("testBoolean", false);
        expected.put("testDateTime", null);
        expected.put("creationDate", null);
        expected.put("testInt", 5);
        expected.put("nestedObject", nestedObject);
        expected.put("version", null);
        expected.put("modificationDate", null);
        expected.put("folder", null);
        expected.put("contents", null);
        expected.put("testString", null);
        expected.put("objectName", null);
        expected.put("nonTable", null);
        expected.put("id", null);
        expected.put("primaryContent", null);
        expected.put("table", null);
        expected.put("testDate", null);

        assertEquals(expected, EcmPropertyUtils.describe(testDocument));
    }

    @Test
    void fieldTest() {
        assertAll(() -> {
            assertNotNull(EcmPropertyUtils.getField(TestDocument.class, "nestedObject.nestedField"));
            assertNotNull(EcmPropertyUtils.getField(TestDocument.class, "testInt"));
            assertNull(EcmPropertyUtils.getField(TestDocument.class, "nonExisting"));
            assertNull(EcmPropertyUtils.getField(TestDocument.class, "table[0].objectName"));
            assertNotNull(EcmPropertyUtils.getField(TestDocument.class, "creationDate"));
        });
    }
}