package com.avispa.ecm.model.configuration;

import com.avispa.ecm.model.EcmEntityRepository;

import java.util.Optional;

/**
 * @author Rafał Hiszpański
 */
public interface EcmConfigRepository<T extends EcmConfig> extends EcmEntityRepository<T> {
    Optional<T> findByObjectName(String objectName);
}
