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

package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.load.dto.DictionaryDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class ConfigurationRegistryTest {
    private static final ConfigurationType TEST = ConfigurationType.of("ecm_test", DictionaryDto.class, false);

    @Test
    void givenConfigType_whenAdd_thenAddedAsLast() {
        ConfigurationRegistry configurationRegistry = new ConfigurationRegistry();

        configurationRegistry.register(TEST);

        ConfigurationType configuration = getLast(configurationRegistry);

        assertEquals(8, configurationRegistry.size());
        assertEquals(TEST, configuration);
    }

    @Test
    void givenConfigType_whenAddBeforeSpecific_thenAddedBefore() {
        ConfigurationRegistry configurationRegistry = new ConfigurationRegistry();

        configurationRegistry.register(TEST, "ecm_context");

        int index = findIndex(configurationRegistry, "ecm_test");

        assertEquals(8, configurationRegistry.size());
        assertEquals(configurationRegistry.size() - 2, index);
    }

    @Test
    void givenConfigType_whenAddBeforeSpecificButItDoesNotExist_thenAddToTheEnd() {
        ConfigurationRegistry configurationRegistry = new ConfigurationRegistry();

        configurationRegistry.register(TEST, "unknown");

        int index = findIndex(configurationRegistry, "ecm_test");

        assertEquals(configurationRegistry.size() - 1, index);
    }

    private static ConfigurationType getLast(ConfigurationRegistry configurationRegistry) {
        var it = configurationRegistry.listIterator(configurationRegistry.size());
        if (it.hasPrevious()) {
            return it.previous();
        }

        return null;
    }

    private static int findIndex(ConfigurationRegistry configurationRegistry, String searchedConfig) {
        int index = 0;
        var it = configurationRegistry.listIterator();

        while (it.hasNext()) {
            if (searchedConfig.equals(it.next().getName())) {
                return index;
            }
            index++;
        }

        return -1;
    }
}