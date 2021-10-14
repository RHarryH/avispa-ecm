package com.avispa.ecm.model.content;

import com.avispa.ecm.model.document.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.UUID;

import static com.avispa.ecm.util.Formats.PDF;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;

    public Content createNewContent(String extension, Document document, Path fileStorePath) {
        Content content = new Content();
        content.setExtension(extension);
        content.setDocument(document);
        content.setFileStorePath(fileStorePath.toString());

        return contentRepository.save(content);
    }

    public Content findRenditionByDocumentId(UUID id) {
        return contentRepository.findByDocumentIdAndExtension(id, PDF);
    }

    public void deleteByDocument(Document document) {
        contentRepository.deleteByDocument(document);
    }
}
