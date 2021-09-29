package com.avispa.cms.model.content;

import com.avispa.cms.model.CmsObjectRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface ContentRepository extends CmsObjectRepository<Content> {
    Content findByDocumentIdAndExtension(UUID id, String extension);
}
