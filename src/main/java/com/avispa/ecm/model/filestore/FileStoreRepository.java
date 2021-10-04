package com.avispa.ecm.model.filestore;

import com.avispa.ecm.model.EcmObjectRepository;

/**
 * @author Rafał Hiszpański
 */
public interface FileStoreRepository extends EcmObjectRepository<FileStore> {
    FileStore findByObjectName(String objectName);
}
