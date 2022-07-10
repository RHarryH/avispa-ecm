package com.avispa.ecm.model.content;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.format.Format;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface ContentRepository extends EcmObjectRepository<Content> {
    boolean existsByRelatedEntityId(UUID id);
    Content findByRelatedEntityIdAndFormat(UUID id, Format format);

    void deleteByRelatedEntity(EcmObject relatedObject);
}
