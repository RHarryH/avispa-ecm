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

package com.avispa.ecm.model.configuration.propertypage.content;

import com.avispa.ecm.model.configuration.propertypage.content.control.Hidden;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class PropertyPageContentTest {
    @Test
    void givenEmptyPropertyPageContent_whenAddHidden_thenThrowException() {
        var content = new PropertyPageContent();
        assertThrows(IllegalStateException.class, () -> content.addHiddenControl("test", "test"));
    }

    @Test
    void givenCorrectPropertyPageContent_whenAddHidden_thenNewControlAppears() {
        var content = new PropertyPageContent();
        content.setControls(new ArrayList<>());
        content.addHiddenControl("test", "test");

        assertFalse(content.getControls().isEmpty());
        assertInstanceOf(Hidden.class, content.getControls().get(0));
    }
}