package com.avispa.ecm.util.expression.function.impl;

import com.avispa.ecm.util.expression.SuperDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class DateValueTest {
    private final DateValue dateValue = new DateValue();

    private static SuperDocument document;

    @BeforeAll
    static void init() {
        document = new SuperDocument();
        document.setExtraDate(LocalDateTime.of(2021, 10, 11, 10, 54, 18));
    }

    @Test
    void noPattern() {
        assertEquals("", dateValue.resolve(document, new String[]{"extraDate", ""}));
    }

    @Test
    void simplePattern() {
        assertEquals("10", dateValue.resolve(document, new String[]{"extraDate", "MM"}));
    }

    @Test
    void complexPattern() {
        assertEquals("2021-10-11 10:54:18", dateValue.resolve(document, new String[]{"extraDate", "yyyy-MM-dd HH:mm:ss"}));
    }
}