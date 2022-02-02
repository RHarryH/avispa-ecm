package com.avispa.ecm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
public interface EcmEntity extends Serializable {
    UUID getId();
    void setId(UUID id);

    String getObjectName();
    void setObjectName(String objectName);

    LocalDateTime getCreationDate();
    LocalDateTime getModificationDate();
}
