package com.avispa.ecm.util.expression.function.impl;

import com.avispa.ecm.util.SuperDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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
        document.setExtraDate(LocalDate.of(2020, 9, 5));
        document.setExtraDateTime(LocalDateTime.of(2021, 10, 11, 10, 54, 18));
    }

    @Test
    void noPattern() {
        assertEquals("", dateValue.resolve(document, new String[]{"extraDateTime", ""}));
    }

    @Test
    void simplePattern() {
        assertEquals("10", dateValue.resolve(document, new String[]{"extraDateTime", "MM"}));
    }

    @Test
    void complexPattern() {
        assertEquals("2021-10-11 10:54:18", dateValue.resolve(document, new String[]{"extraDateTime", "yyyy-MM-dd HH:mm:ss"}));
    }

    @Test
    void localDateOnly() {
        assertEquals("2020/09", dateValue.resolve(document, new String[]{"extraDate", "yyyy/MM"}));
    }
}