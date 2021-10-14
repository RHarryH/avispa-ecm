package com.avispa.ecm.model.content;

import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.document.Document;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface ContentRepository extends EcmObjectRepository<Content> {
    Content findByDocumentIdAndExtension(UUID id, String extension);

    void deleteByDocument(Document document);
}
