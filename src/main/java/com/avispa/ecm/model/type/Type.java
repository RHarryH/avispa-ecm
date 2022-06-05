package com.avispa.ecm.model.type;

import com.avispa.ecm.model.EcmObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Represents the name of the type and bounded class
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
public class Type extends EcmObject {
    @Column(name = "class_name", nullable = false, unique = true)
    private Class<? extends EcmObject> entityClass;
}
