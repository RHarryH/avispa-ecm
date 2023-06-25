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

package com.avispa.ecm.service.rendition;

import com.avispa.ecm.model.EcmEntity;
import com.avispa.ecm.model.content.Content;
import com.avispa.ecm.model.content.ContentRepository;
import com.avispa.ecm.model.content.ContentService;
import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.filestore.FileStore;
import com.avispa.ecm.model.format.Format;
import com.avispa.ecm.model.format.FormatRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.ExistingProcessAction;
import org.jodconverter.local.office.LocalOfficeManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(MockitoExtension.class)
class RenditionServiceTest {
    @Mock
    private FormatRepository formatRepository;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentService;

    private RenditionService renditionService;
    private final OfficeManager officeManager;
    private final DocumentConverter documentConverter;
    private final FileStore fileStore;

    public RenditionServiceTest() throws IOException {
        this.fileStore = getFileStore();

        this.officeManager = LocalOfficeManager.builder()
                .officeHome(SystemUtils.IS_OS_LINUX ? "/usr/lib/libreoffice" : "C:\\Program Files\\LibreOffice")
                .existingProcessAction(ExistingProcessAction.CONNECT_OR_KILL)
                .build();

        this.documentConverter = LocalConverter.builder()
                .officeManager(officeManager)
                .build();
    }

    public FileStore getFileStore() throws IOException {
        FileStore fileStore = new FileStore();
        fileStore.setObjectName("Test file store");
        fileStore.setRootPath("target/rendition-test");

        createFileStorePath(fileStore);

        return fileStore;
    }

    private void createFileStorePath(FileStore fileStore) throws IOException {
        Path fp = Paths.get(fileStore.getRootPath());
        Files.createDirectories(fp);
    }

    @BeforeEach
    public void setUp() throws OfficeException {
        this.renditionService = new RenditionService(fileStore, contentService, documentConverter);

        when(formatRepository.findByExtension("pdf")).thenReturn(getPdfFormat());
        when(contentRepository.save(any(Content.class))).thenAnswer(i -> i.getArgument(0));

        officeManager.start();

        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    public void tearDown() throws OfficeException, IOException {
        officeManager.stop();

        FileUtils.cleanDirectory(new File(fileStore.getRootPath()));

        TransactionSynchronizationManager.clear();
    }

    @Test
    void givenTestFile_whenGeneratingPdf_thenFileExists() throws ExecutionException, InterruptedException {
        Content inputContent = getInputContent();
        var result = renditionService.generate(inputContent);

        Content rendition = result.get();

        assertTrue(rendition.isPdf());
        assertTrue(new File(rendition.getFileStorePath()).exists());
    }

    private Content getInputContent() {
        Format format = getOdtFormat();

        EcmEntity document = new Document();
        document.setId(UUID.randomUUID());
        document.setObjectName("Document");

        Content content = new Content();
        content.setId(UUID.randomUUID());
        content.setObjectName("Content");
        content.setFormat(format);
        content.setRelatedEntity(document);
        content.setFileStorePath("src/test/resources/document/test.odt");
        return content;
    }

    private Format getOdtFormat() {
        Format format = new Format();
        format.setId(UUID.randomUUID());
        format.setObjectName(Format.ODT);
        format.setMimeType("application/vnd.oasis.opendocument.text");
        format.setDescription("OpenDocument text document");
        return format;
    }

    private Format getPdfFormat() {
        Format format = new Format();
        format.setId(UUID.randomUUID());
        format.setObjectName(Format.PDF);
        format.setMimeType("application/pdf");
        format.setDescription("Adobe Portable Document Format (PDF)");
        return format;
    }
}