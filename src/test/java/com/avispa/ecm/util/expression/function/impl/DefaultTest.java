package com.avispa.ecm.util.expression.function.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class DefaultTest {
    private final Default defaultFunction = new Default();

    @Test
    void getOriginalValue() {
        assertEquals("originalValue", defaultFunction.resolve(null, new String[]{"originalValue", "defaultValue"}));
    }

    @Test
    void getDefaultValue() {
        assertEquals("defaultValue", defaultFunction.resolve(null, new String[]{null, "defaultValue"}));
    }
}