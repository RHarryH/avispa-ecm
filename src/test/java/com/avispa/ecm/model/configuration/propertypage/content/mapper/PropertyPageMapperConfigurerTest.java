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

package com.avispa.ecm.model.configuration.propertypage.content.mapper;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
class PropertyPageMapperConfigurerTest {
    @Test
    void readonlyTest() {
        var configurer = PropertyPageMapperConfigurer.readonly();

        assertAll(() -> {
            assertTrue(configurer.isReadonly());
            assertEquals(List.of("id"), configurer.getFillBlacklist());
        });
    }

    @Test
    void insertTest() {
        var configurer = PropertyPageMapperConfigurer.insert();

        assertAll(() -> {
            assertFalse(configurer.isReadonly());
            assertEquals(List.of("id"), configurer.getFillBlacklist());
        });
    }

    @Test
    void editTest() {
        var configurer = PropertyPageMapperConfigurer.edit();

        assertAll(() -> {
            assertFalse(configurer.isReadonly());
            assertTrue(configurer.getFillBlacklist().isEmpty());
        });
    }
}