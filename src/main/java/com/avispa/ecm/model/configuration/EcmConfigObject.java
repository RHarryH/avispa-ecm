package com.avispa.ecm.model.configuration;

import com.avispa.ecm.model.EcmEntity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class EcmConfigObject extends EcmEntity {
}
