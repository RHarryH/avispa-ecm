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

import com.avispa.ecm.model.content.Content;
import com.avispa.ecm.model.content.ContentService;
import com.avispa.ecm.model.filestore.FileStore;
import com.avispa.ecm.model.format.Format;
import com.avispa.ecm.model.format.FormatNotFoundException;
import com.avispa.ecm.util.exception.EcmException;
import com.avispa.ecm.util.transaction.TransactionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.Future;

import static com.avispa.ecm.model.format.Format.PDF;

/**
 * @author Rafał Hiszpański
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RenditionService {
    private final FileStore fileStore;
    private final ContentService contentService;

    private final DocumentConverter documentConverter;

    /**
     * Generate PDF rendition based on the input file
     * @param content
     */
    @Async
    @Transactional
    public Future<Content> generate(Content content) {
        log.info("Requested PDF rendition");

        Format format = content.getFormat();

        if(format.isPdf()) {
            log.warn("Document is already a pdf. Ignoring");
            return new AsyncResult<>(null);
        }

        Path renditionFileStorePath = Path.of(fileStore.getRootPath(), UUID.randomUUID().toString());
        TransactionUtils.registerFileRollback(renditionFileStorePath);

        try(InputStream inputStream = new FileInputStream(content.getFileStorePath());
            OutputStream outputStream = new FileOutputStream(renditionFileStorePath.toString())) {
            String extension = format.getExtension();

            generateWithSOffice(extension, inputStream, outputStream);

            Content rendition = contentService.createNewContent(PDF, content.getRelatedEntity(), renditionFileStorePath);

            log.info("PDF rendition generated successfully");

            return new AsyncResult<>(rendition);
        } catch (Exception e) {
            String errorMessage = "PDF rendition cannot be generated";
            log.error(errorMessage, e);
            throw new EcmException(errorMessage);
        }
    }

    /**
     * Generates pdf rendition using soffice from LibreOffice or OpenOffice using JODConverter helper library
     * @param extension source extension
     * @param inputStream original file stream
     * @param outputStream rendition file stream
     * @throws OfficeException
     * @throws FormatNotFoundException
     */
    private void generateWithSOffice(String extension, InputStream inputStream, OutputStream outputStream) throws OfficeException, FormatNotFoundException {
        final DocumentFormat sourceFormat =
                DefaultDocumentFormatRegistry.getFormatByExtension(extension);
        if(null == sourceFormat) {
            throw new FormatNotFoundException("Extension not supported by the rendition service: " + extension);
        }

        final DocumentFormat targetFormat =
                DefaultDocumentFormatRegistry.getFormatByExtension(PDF);
        if(null == targetFormat) {
            throw new FormatNotFoundException("Can't find PDF format");
        }

        documentConverter
                .convert(inputStream)
                .as(sourceFormat)
                .to(outputStream)
                .as(targetFormat)
                .execute();
    }
}
