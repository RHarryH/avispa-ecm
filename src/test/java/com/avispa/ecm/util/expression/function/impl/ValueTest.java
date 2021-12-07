package com.avispa.ecm.util.expression.function.impl;

import com.avispa.ecm.util.SuperDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class ValueTest {
    private static SuperDocument document;
    private final Value value = new Value();

    @BeforeAll
    static void init() {
        document = new SuperDocument();
        document.setObjectName("ABC");
        document.setExtraDateTime(LocalDateTime.of(2021, 10, 11, 10, 54, 18));
        document.setExtraInt(5);
    }

    @Test
    void getStringValue() {
        assertEquals("ABC", value.resolve(document, new String[] {"objectName"}));
    }

    @Test
    void getIntValue() {
        assertEquals("5", value.resolve(document, new String[] {"extraInt"}));
    }

    @Test
    void getDateValue() {
        assertEquals("2021-10-11T10:54:18", value.resolve(document, new String[] {"extraDateTime"}));
    }

    @Test
    void nonExistingValue() {
        assertEquals("", value.resolve(document, new String[] {"doesNotExist"}));
    }
}