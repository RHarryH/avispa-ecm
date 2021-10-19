package com.avispa.ecm.model.document;

import com.avispa.ecm.model.EcmObjectRepository;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public interface DocumentRepository extends EcmObjectRepository<Document> {
    List<Document> findAllByFolderNotNull();
}
