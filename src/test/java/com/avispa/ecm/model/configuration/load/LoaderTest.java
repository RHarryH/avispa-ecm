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

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.callable.autolink.Autolink;
import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.dictionary.DictionaryValue;
import com.avispa.ecm.model.configuration.load.dto.AutolinkDto;
import com.avispa.ecm.model.configuration.load.dto.AutonameDto;
import com.avispa.ecm.model.configuration.load.dto.DictionaryDto;
import com.avispa.ecm.model.configuration.load.dto.DictionaryValueDto;
import com.avispa.ecm.model.configuration.load.mapper.AutolinkMapperImpl;
import com.avispa.ecm.model.configuration.load.mapper.AutonameMapperImpl;
import com.avispa.ecm.model.configuration.load.mapper.DictionaryMapperImpl;
import com.avispa.ecm.model.configuration.load.mapper.DictionaryValueMapperImpl;
import com.avispa.ecm.model.content.Content;
import com.avispa.ecm.model.content.ContentService;
import com.avispa.ecm.model.filestore.FileStore;
import com.avispa.ecm.model.format.Format;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Rafał Hiszpański
 */
@DataJpaTest
@Import({
        AutolinkMapperImpl.class, AutolinkLoader.class,
        AutonameMapperImpl.class, AutonameLoader.class,
        DictionaryMapperImpl.class, DictionaryValueMapperImpl.class, DictionaryLoader.class,
        ContentService.class})
@Slf4j
class LoaderTest {
    @Autowired
    private EcmObjectRepository<EcmObject> ecmObjectRepository;

    private static final String AUTOLINK_NAME = "Autolink";
    private static final String DICTIONARY_NAME = "Dictionary";
    private static final String TEST_STORE_PATH = "src/test/resources/test-store";

    @Autowired
    private EcmConfigRepository<EcmConfig> ecmConfigRepository;

    @Autowired
    private AutolinkLoader autolinkLoader;

    @Autowired
    private AutonameLoader autonameLoader;

    @Autowired
    private DictionaryLoader dictionaryLoader;

    @MockBean
    private FileStore fileStore;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Files.createDirectory(Path.of(TEST_STORE_PATH));
    }

    @AfterAll
    public static void afterAll() throws IOException {
        FileSystemUtils.deleteRecursively(Path.of(TEST_STORE_PATH));
    }

    @Test
    void givenLoader_whenDtoIsCorrect_thenInsertIsSuccessful() {
        // given
        var autolinkDto = new AutolinkDto();
        autolinkDto.setName(AUTOLINK_NAME);

        // when
        autolinkLoader.load(autolinkDto, false);

        // then
        ecmConfigRepository.findByObjectName(AUTOLINK_NAME).ifPresentOrElse(
                autolink -> assertEquals(AUTOLINK_NAME, autolink.getObjectName()),
                Assertions::fail
        );
    }

    @Test
    void givenLoader_whenDtoIsInvalid_thenInsertFails() {
        // given
        var autonameDto = new AutonameDto();

        // when
        autonameLoader.load(autonameDto, false);

        // then
        assertThrows(DataIntegrityViolationException.class, () -> ecmConfigRepository.flush());
    }

    @Test
    void dontUpdateExistingConfigWhenOverwriteIsForbidden() {
        // given
        var autolinkDto = new AutolinkDto();
        autolinkDto.setName(AUTOLINK_NAME);
        autolinkDto.setDefaultValue("DefaultDTO");

        var autolinkEntity = new Autolink();
        autolinkEntity.setObjectName(AUTOLINK_NAME);
        autolinkEntity.setDefaultValue("DefaultOriginal");
        ecmConfigRepository.save(autolinkEntity);

        // when
        autolinkLoader.load(autolinkDto, false);

        // then
        ecmConfigRepository.findByObjectName(AUTOLINK_NAME).ifPresentOrElse(
                config -> {
                    var autolink = (Autolink)config;
                    assertEquals("DefaultOriginal", autolink.getDefaultValue());
                },
                Assertions::fail
        );
    }

    @Test
    void updateExistingConfigWhenOverwriteIsAllowed() {
        // given
        var autolinkEntity = new Autolink();
        autolinkEntity.setObjectName(AUTOLINK_NAME);
        autolinkEntity.setDefaultValue("DefaultOriginal");
        ecmConfigRepository.save(autolinkEntity);

        var autolinkDto = new AutolinkDto();
        autolinkDto.setName(AUTOLINK_NAME);
        autolinkDto.setDefaultValue("DefaultDTO");

        // when
        autolinkLoader.load(autolinkDto, true);

        // then
        ecmConfigRepository.findByObjectName(AUTOLINK_NAME).ifPresentOrElse(
                config -> {
                    var autolink = (Autolink)config;
                    assertEquals("DefaultDTO", autolink.getDefaultValue());
                },
                Assertions::fail
        );
    }

    @Test
    void updateExistingDictionaryWithClearedValuesWhenOverwriteIsAllowed() {
        // given
        var dictionaryEntity = new Dictionary();
        dictionaryEntity.setObjectName(DICTIONARY_NAME);
        dictionaryEntity.setDescription("Desc");

        var dictionaryValue = new DictionaryValue();
        dictionaryValue.setKey("key1");
        dictionaryValue.setLabel("value1");

        dictionaryEntity.addValue(dictionaryValue);

        var dictionaryDto = new DictionaryDto();
        dictionaryDto.setName(DICTIONARY_NAME);
        dictionaryDto.setDescription("New desc");

        var dictionaryValueDto = getDictionaryValueDto("key2", "value2");

        dictionaryDto.setValues(List.of(dictionaryValueDto));

        ecmConfigRepository.save(dictionaryEntity);

        // when
        dictionaryLoader.load(dictionaryDto, true);

        // then
        ecmConfigRepository.findByObjectName(DICTIONARY_NAME).ifPresentOrElse(
                config -> {
                    var dictionary = (Dictionary)config;
                    assertEquals("New desc", dictionary.getDescription());
                    var dictionaryValues = dictionary.getValues();
                    assertEquals(1, dictionaryValues.size());
                    assertEquals("key2", dictionaryValues.get(0).getKey());
                },
                Assertions::fail
        );
    }

    @Test
    void removeOldContentAndUploadNewWhenOverwriteIsEnabled() {
        // given
        var autolinkDto = new AutolinkDto();
        autolinkDto.setName(AUTOLINK_NAME);
        autolinkDto.setDefaultValue("DefaultDTO");

        // test format
        var format = new Format();
        format.setObjectName("Some format");
        ecmObjectRepository.save(format);

        // existing content
        var contentEntity = getNewContent(format);

        // existing autolink configuration
        var autolinkEntity = new Autolink();
        autolinkEntity.setObjectName(AUTOLINK_NAME);
        autolinkEntity.setDefaultValue("DefaultOriginal");

        contentEntity.setRelatedEntity(autolinkEntity);
        autolinkEntity.addContent(contentEntity);

        ecmConfigRepository.save(autolinkEntity);

        when(fileStore.getRootPath()).thenReturn(Path.of(TEST_STORE_PATH).toString());

        // when
        autolinkLoader.load(autolinkDto, Path.of("src/test/resources/document/test.odt"), true);

        // then
        ecmConfigRepository.findByObjectName(AUTOLINK_NAME).ifPresentOrElse(
                config -> {
                    var autolink = (Autolink)config;
                    assertEquals(1, autolink.getContents().size());
                    autolink.getContents().forEach(content -> assertEquals("Autolink.odt", content.getObjectName()));
                },
                Assertions::fail
        );
    }

    private Content getNewContent(Format format) {
        Content content = new Content();
        content.setObjectName("Some content");
        content.setFormat(format);
        content.setFileStorePath("Test");

        return content;
    }

    @Test
    void dictionaryLoadingTest() {
        // given
        var dictionaryDto = new DictionaryDto();
        dictionaryDto.setName(DICTIONARY_NAME);
        dictionaryDto.setDescription("Some dictionary");

        DictionaryValueDto dictionaryValue1Dto = getDictionaryValueDto("key1", "value1");
        DictionaryValueDto dictionaryValue2Dto = getDictionaryValueDto("key2", "value2");

        dictionaryDto.setValues(List.of(dictionaryValue1Dto, dictionaryValue2Dto));

        // when
        dictionaryLoader.load(dictionaryDto, false);

        // then
        ecmConfigRepository.findByObjectName(DICTIONARY_NAME).ifPresentOrElse(
                config -> {
                    var dictionary = (Dictionary)config;
                    assertEquals("Some dictionary", dictionary.getDescription());

                    var dictionaryValues = dictionary.getValues();
                    assertEquals(2, dictionaryValues.size());

                    assertEquals("key1", dictionaryValues.get(0).getKey());
                    assertEquals("value1", dictionaryValues.get(0).getLabel());

                    assertEquals("key2", dictionaryValues.get(1).getKey());
                    assertEquals("value2", dictionaryValues.get(1).getLabel());
                },
                Assertions::fail
        );
    }

    private static DictionaryValueDto getDictionaryValueDto(String key, String value) {
        var dictionaryValueDto = new DictionaryValueDto();
        dictionaryValueDto.setKey(key);
        dictionaryValueDto.setLabel(value);

        return dictionaryValueDto;
    }
}