package com.avispa.cms.model.content;

import com.avispa.cms.model.CmsObjectRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface ContentRepository extends CmsObjectRepository<Content> {
    Content findByDocumentIdAndExtension(Long id, String extension);
}
