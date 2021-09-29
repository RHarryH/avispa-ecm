package com.avispa.cms.model.filestore;

import com.avispa.cms.model.CmsObjectRepository;

/**
 * @author Rafał Hiszpański
 */
public interface FileStoreRepository extends CmsObjectRepository<FileStore> {
    FileStore findByObjectName(String objectName);
}
