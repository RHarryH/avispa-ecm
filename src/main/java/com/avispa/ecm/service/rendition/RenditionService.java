package com.avispa.ecm.service.rendition;

import com.avispa.ecm.model.content.Content;
import com.avispa.ecm.model.content.ContentService;
import com.avispa.ecm.model.filestore.FileStore;
import com.avispa.ecm.model.format.Format;
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.UUID;

import static com.avispa.ecm.model.format.Format.DOCX;
import static com.avispa.ecm.model.format.Format.ODT;
import static com.avispa.ecm.model.format.Format.PDF;

/**
 * @author Rafał Hiszpański
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RenditionService {
    private final OfficeManager officeManager;
    private final FileStore fileStore;
    private final ContentService contentService;

    @Value("${avispa.ecm.rendition.use-always-office:true}")
    private boolean renditionUseAlwaysOffice;

    /**
     * Generate PDF rendition based on the input file
     * @param content
     */
    @Async
    public void generate(Content content) {
        log.info("Requested PDF rendition");

        Format format = content.getFormat();

        if(format.isPdf()) {
            log.warn("Document is already a pdf. Ignoring");
            return;
        }

        Path renditionFileStorePath = Path.of(fileStore.getRootPath(), UUID.randomUUID().toString());

        try  {
            try(InputStream inputStream = new FileInputStream(content.getFileStorePath());
                OutputStream outputStream = new FileOutputStream(renditionFileStorePath.toString())) {
                String extension = format.getExtension();

                if (renditionUseAlwaysOffice) {
                    generateUsingSoffice(extension, inputStream, outputStream);
                } else {
                    switch (extension) {
                        case DOCX: // does not require soffice
                            IConverter converter = LocalConverter.builder().build();
                            converter.convert(inputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
                        break;
                        case ODT:
                            generateUsingSoffice(extension, inputStream, outputStream);
                            break;
                        default:
                            log.error("Unsupported extension: {}.", extension);

                    }
                }
            }

            contentService.createNewContent(PDF, content.getRelatedObject(), renditionFileStorePath);

            log.info("PDF rendition generated successfully");
        } catch (Exception e) {
            log.error("PDF rendition cannot be generated", e);
        }
    }

    /**
     * Generates pdf rendition using soffice from LibreOffice or OpenOffice using JODConverter helper library
     * @param extension source extension
     * @param inputStream original file stream
     * @param outputStream rendition file stream
     * @throws OfficeException
     */
    private void generateUsingSoffice(String extension, InputStream inputStream, OutputStream outputStream) throws OfficeException {
        final DocumentFormat targetFormat =
                DefaultDocumentFormatRegistry.getFormatByExtension(PDF);

        org.jodconverter.local.LocalConverter
                .make(officeManager)
                .convert(inputStream)
                .as(DefaultDocumentFormatRegistry.getFormatByExtension(
                        extension))
                .to(outputStream)
                .as(targetFormat)
                .execute();
    }
}
