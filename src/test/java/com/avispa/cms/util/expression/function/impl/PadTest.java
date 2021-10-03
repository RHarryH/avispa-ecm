package com.avispa.cms.util.expression.function.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class PadTest {
    private final Pad pad = new Pad();

    @Test
    void simplePadding() {
        assertEquals("01", pad.resolve(null, new String[]{"1", "2"}));
    }

    @Test
    void numberOfCharactersLessThanInput() {
        assertEquals("abc", pad.resolve(null, new String[]{"abc", "1"}));
    }

    @Test
    void customCharacterPadding() {
        assertEquals("Aabc", pad.resolve(null, new String[]{"abc", "4", "A"}));
    }

    @Test
    void emptyPaddingCharacter() {
        assertEquals("003", pad.resolve(null, new String[]{"3", "3", ""}));
    }

    @Test
    void numberOfCharactersIsNotAnInteger() {
        assertEquals("3", pad.resolve(null, new String[]{"3", "not-an-integer"}));
    }

    @Test
    void negativeNumberOfCharacters() {
        assertEquals("1234", pad.resolve(null, new String[]{"1234", "-10"}));
    }
}