package com.avispa.ecm.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ecm_object")
public abstract class EcmObject extends EcmEntity {
}
