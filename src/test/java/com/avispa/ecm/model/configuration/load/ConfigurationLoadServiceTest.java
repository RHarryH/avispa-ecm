package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.load.dto.AutolinkDto;
import com.avispa.ecm.model.configuration.load.dto.DictionaryDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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