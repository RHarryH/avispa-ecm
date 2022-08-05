package com.avispa.ecm.util.expression.function.impl;

import com.avispa.ecm.util.TestDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class ValueTest {
    private static TestDocument document;
    private final Value value = new Value();

    @BeforeAll
    static void init() {
        document = new TestDocument();
        document.setObjectName("ABC");
        document.setTestDateTime(LocalDateTime.of(2021, 10, 11, 10, 54, 18));
        document.setTestInt(5);
    }

    @Test
    void getStringValue() {
        assertEquals("ABC", value.resolve(document, new String[] {"objectName"}));
    }

    @Test
    void getIntValue() {
        assertEquals("5", value.resolve(document, new String[] {"testInt"}));
    }

    @Test
    void getDateValue() {
        assertEquals("2021-10-11T10:54:18", value.resolve(document, new String[] {"testDateTime"}));
    }

    @Test
    void nonExistingValue() {
        assertEquals("", value.resolve(document, new String[] {"doesNotExist"}));
    }
}