package com.avispa.ecm.model.content;

import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.model.format.Format;
import com.avispa.ecm.model.format.FormatNotFoundException;
import com.avispa.ecm.model.format.FormatRepository;
import com.avispa.ecm.util.exception.RepositoryCorruptionError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.UUID;

import static com.avispa.ecm.model.format.Format.PDF;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {
    private final ContentRepository contentRepository;
    private final FormatRepository formatRepository;

    public Content createNewContent(String extension, Document document, Path fileStorePath) {
        Format format = formatRepository.findByExtension(extension);

        Content content = new Content();
        content.setObjectName(document.getObjectName().replace("/","_") + "." + extension);
        content.setFormat(format);
        content.setDocument(document);
        content.setFileStorePath(fileStorePath.toString());

        return contentRepository.save(content);
    }

    public Content findPdfRenditionByDocumentId(UUID id) {
        try {
            return contentRepository.findByDocumentIdAndFormat(id, formatRepository.findByExtensionOrThrowException(PDF));
        } catch (FormatNotFoundException e) {
            throw new RepositoryCorruptionError("PDF Format not found in ECM Repository. Probably it is corrupted.");
        }
    }

    public void deleteByDocument(Document document) {
        contentRepository.deleteByDocument(document);
    }
}
