package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.load.dto.ContextDto;
import com.avispa.ecm.model.configuration.load.dto.PropertyPageDto;
import com.avispa.ecm.util.exception.EcmConfigurationException;
import com.avispa.ecm.util.json.JsonValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
class ConfigurationReaderTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JsonValidator jsonValidator = new JsonValidator(objectMapper);

    private final ConfigurationReader configurationReader = new ConfigurationReader(objectMapper, jsonValidator);

    @Test
    void givenConfiguration_whenRead_thenAllItemsCreated() {
        Configuration config = configurationReader.read(Path.of("src/test/resources/configuration/basic-configuration.zip"));

        var configItems = config.getConfigDtos();
        var contents = config.getContents();

        PropertyPageDto documentPPDto = new PropertyPageDto();
        documentPPDto.setName("Document property page");

        PropertyPageDto folderPPDto = new PropertyPageDto();
        folderPPDto.setName("Folder property page");

        ContextDto documentContextDto = new ContextDto();
        documentContextDto.setName("Document context");
        documentContextDto.setType("Document");
        documentContextDto.setImportance(1);
        documentContextDto.setConfigNames(List.of("Document property page"));

        ContextDto folderContextDto = new ContextDto();
        folderContextDto.setName("Folder context");
        folderContextDto.setType("Folder");
        folderContextDto.setImportance(0);
        folderContextDto.setConfigNames(List.of("Folder property page"));

        var expectedItems = List.of(folderPPDto, documentPPDto, folderContextDto, documentContextDto);

        assertEquals(4, configItems.size());
        assertEquals(2, contents.size());
        for(int i = 0; i < configItems.size(); i++) {
            assertEquals(expectedItems.get(i), configItems.get(i));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "non-existing-configuration.zip", // file does not exist physically
            "missing-content-configuration.zip", // missing content when required
            "incorrect-folder-configuration.zip", // there is no content folder when required
            "invalid-configuration.zip" // configuration JSON can't be parsed
    })
    void givenInvalidConfiguration_whenRead_thenThrowException(String configFileName) {
        Path zipConfigPath = Path.of("src/test/resources/configuration/" + configFileName);
        assertThrows(EcmConfigurationException.class, () -> configurationReader.read(zipConfigPath));
    }

    @Test
    void givenConfigurationWithFileInRoot_whenRead_thenNothingRead() {
        Path zipConfigPath = Path.of("src/test/resources/configuration/file-in-root-configuration.zip");
        Configuration configuration = configurationReader.read(zipConfigPath);

        assertTrue(configuration.getConfigDtos().isEmpty());
    }

    @Test
    void givenConfigurationUnknownType_whenRead_thenNothingRead() {
        Path zipConfigPath = Path.of("src/test/resources/configuration/unknown-configuration.zip");
        Configuration configuration = configurationReader.read(zipConfigPath);

        assertTrue(configuration.getConfigDtos().isEmpty());
    }

    @Test
    void givenConfigurationWithNotRequiredContent_whenRead_thenContentWasNotRead() {
        Path zipConfigPath = Path.of("src/test/resources/configuration/not-required-content-configuration.zip");
        Configuration configuration = configurationReader.read(zipConfigPath);

        assertTrue(configuration.getContents().isEmpty());
        assertEquals(1, configuration.getConfigDtos().size());
    }
}