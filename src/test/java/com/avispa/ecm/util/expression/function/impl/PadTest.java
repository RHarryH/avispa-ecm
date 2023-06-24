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

package com.avispa.ecm.util.expression.function.impl;

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