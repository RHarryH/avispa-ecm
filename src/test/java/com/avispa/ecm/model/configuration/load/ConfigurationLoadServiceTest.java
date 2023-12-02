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

package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.load.dto.AutolinkDto;
import com.avispa.ecm.model.configuration.load.dto.DictionaryDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
class ConfigurationLoadServiceTest {
    private static final String AUTOLINK_NAME = "Autolink";

    @Mock
    private ConfigurationReader configurationReader;

    private ConfigurationLoadService configurationLoadService;

    @Mock
    private AutolinkLoader autolinkLoader;

    @BeforeEach
    public void init() {
        configurationLoadService = new ConfigurationLoadService(configurationReader, Map.of("autolinkLoader", autolinkLoader));
    }

    @Test
    void verifyIfLoaderWasCalled() {
        Configuration configuration = new Configuration();

        AutolinkDto autolinkDto = new AutolinkDto();
        autolinkDto.setName(AUTOLINK_NAME);

        configuration.addConfigDto(autolinkDto);

        when(configurationReader.read(any(Path.class))).thenReturn(configuration);
        configurationLoadService.load(Path.of("src/test/resources/configuration/basic-configuration.zip"), true);

        verify(autolinkLoader).load(autolinkDto, true);
    }

    @Test
    void verifyIfLoaderWithContentWasCalled() {
        Configuration configuration = new Configuration();

        AutolinkDto autolinkDto = new AutolinkDto();
        autolinkDto.setName(AUTOLINK_NAME);

        configuration.addConfigDto(autolinkDto);
        Path mockPath = Path.of("Mock path");
        configuration.addContent(AUTOLINK_NAME, mockPath);

        when(configurationReader.read(any(Path.class))).thenReturn(configuration);
        configurationLoadService.load(Path.of("src/test/resources/configuration/basic-configuration.zip"), false);

        verify(autolinkLoader).load(autolinkDto, mockPath, false);
    }

    @Test
    void loaderWasNotFound() {
        Configuration configuration = new Configuration();

        DictionaryDto dictionaryDto = new DictionaryDto();
        dictionaryDto.setName("Dictionary");

        configuration.addConfigDto(dictionaryDto);

        when(configurationReader.read(any(Path.class))).thenReturn(configuration);
        configurationLoadService.load(Path.of("src/test/resources/configuration/basic-configuration.zip"), false);

        verifyNoInteractions(autolinkLoader);
    }
}