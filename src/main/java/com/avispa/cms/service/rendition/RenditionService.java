package com.avispa.cms.service.rendition;

import com.avispa.cms.model.content.Content;
import com.avispa.cms.model.content.ContentRepository;
import com.avispa.cms.model.filestore.FileStore;
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.document.DocumentFormat;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;
import org.jodconverter.office.OfficeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import static com.avispa.cms.util.Formats.DOCX;
import static com.avispa.cms.util.Formats.ODT;
import static com.avispa.cms.util.Formats.PDF;

/**
 * @author Rafał Hiszpański
 */
@Service
@Slf4j
public class RenditionService {
    private OfficeManager officeManager;
    private FileStore fileStore;
    private ContentRepository contentRepository;

    @Value("${rendition.office.always:true}")
    private boolean renditionOfficeAlways;

    @Autowired
    public RenditionService(@Value("${office.home:D:\\LibreOffice}") String officePath, FileStore fileStore, ContentRepository contentRepository) {
        this.officeManager = LocalOfficeManager.builder()
                .officeHome(officePath)
                .processManager("org.jodconverter.process.PureJavaProcessManager")
                .build();

        this.fileStore = fileStore;
        this.contentRepository = contentRepository;
    }

    /**
     * Generate PDF rendition based on the input file
     * @param content
     */
    @Async
    public void generate(Content content) {
        log.info("Requested PDF rendition");

        if(content.getExtension().equals("pdf")) {
            log.warn("Document is already a pdf. Ignoring");
            return;
        }

        String extension = content.getExtension();

        Content rendition = new Content();
        rendition.setExtension(PDF);
        rendition.setDocument(content.getDocument());
        rendition.setFileStorePath(Path.of(fileStore.getRootPath(), rendition.getUuid().toString()).toString());

        try  {
            try(InputStream inputStream = new FileInputStream(content.getFileStorePath());
                OutputStream outputStream = new FileOutputStream(rendition.getFileStorePath())) {

                if (renditionOfficeAlways) {
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

            contentRepository.save(rendition);

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

        try {
            officeManager.start();

            org.jodconverter.LocalConverter
                    .make(officeManager)
                    .convert(inputStream)
                    .as(DefaultDocumentFormatRegistry.getFormatByExtension(
                            extension))
                    .to(outputStream)
                    .as(targetFormat)
                    .execute();
        } finally {
            OfficeUtils.stopQuietly(officeManager);
        }
    }
}
