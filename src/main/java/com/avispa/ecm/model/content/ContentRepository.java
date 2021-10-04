package com.avispa.ecm.model.content;

import com.avispa.ecm.model.EcmObjectRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface ContentRepository extends EcmObjectRepository<Content> {
    Content findByDocumentIdAndExtension(Long id, String extension);
}
