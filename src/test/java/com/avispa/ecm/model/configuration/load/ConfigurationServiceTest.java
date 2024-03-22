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

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.content.ContentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
@SpringBootTest
class ConfigurationServiceTest {
    @Autowired
    private ConfigurationLoadService configurationLoadService;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private EcmConfigRepository<EcmConfig> ecmConfigRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Test
    void clear() {
        configurationLoadService.load(Path.of("src/test/resources/configuration/basic-configuration-with-dictionary.zip"), false);

        assertEquals(2, contentRepository.count());
        assertEquals(9, ecmConfigRepository.count());

        configurationService.clear();

        assertEquals(0, contentRepository.count());
        assertEquals(0, ecmConfigRepository.count());
    }
}