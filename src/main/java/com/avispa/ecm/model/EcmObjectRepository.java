package com.avispa.ecm.model;

import java.util.Optional;

/**
 * @author Rafał Hiszpański
 */
public interface EcmObjectRepository<T extends EcmObject> extends EcmEntityRepository<T> {
    Optional<T> findByObjectName(String objectName);
}
