package com.avispa.cms.model.type;

import com.avispa.cms.model.CmsObject;
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
public class Type extends CmsObject {
    @Column(name = "class_name", nullable = false)
    private Class<? extends CmsObject> clazz;
}
