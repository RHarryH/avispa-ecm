package com.avispa.cms.util.expression;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class ExpressionResolverTest {

    private static SuperDocument document;

    private final ExpressionResolver expressionResolver = new ExpressionResolver();

    @BeforeAll
    static void init() {
        document = new SuperDocument();
        document.setObjectName("ABC");
        document.setExtraDate(LocalDateTime.of(2021, 10, 11, 10, 54, 18));
        document.setExtraField("XY");
        document.setExtraInt(5);
    }

    @Test
    void applyFieldFromSuperDocument() {
        assertEquals("ABC_XY_ABC", expressionResolver.resolve(document, "ABC_$value(extraField)_ABC"));
    }

    @Test
    void applyNonExistingField() {
        assertEquals("ABC__ABC", expressionResolver.resolve(document, "ABC_$value(nonExistingField)_ABC"));
    }

    @Test
    void replaceInteger() {
        assertEquals("ABC_5_ABC", expressionResolver.resolve(document, "ABC_$value(extraInt)_ABC"));
    }

    @Test
    void functionOnly() {
        assertEquals("XY", expressionResolver.resolve(document, "$value(extraField)"));
    }

    @Test
    void insufficientNumberOfArguments() {
        assertThrows(IllegalArgumentException.class, () -> expressionResolver.resolve(document, "ABC_$default(nonExistingField)_ABC"));
    }

    @Test
    void unknownFunction() {
        assertEquals("ABC_$unknownFunction(nonExistingField)_ABC", expressionResolver.resolve(document, "ABC_$unknownFunction(nonExistingField)_ABC"));
    }

    @Test
    void dateValueFunction() {
        assertEquals("ABC_10_ABC", expressionResolver.resolve(document, "ABC_$datevalue(extraDate, MM)_ABC"));
    }

    @Test
    void dateValueFunctionInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> expressionResolver.resolve(document, "ABC_$datevalue(extraDate, invalid_format)_ABC"));
    }

    @Test
    void nestedFunction() {
        assertEquals("ABC_XY_ABC", expressionResolver.resolve(document, "ABC_$default($value(extraField), This is default value)_ABC"));
    }
}