package com.avispa.cms.model.configuration.autoname;

import com.avispa.cms.model.configuration.CmsConfigObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
public class Autoname extends CmsConfigObject {
    private String rule;
    private String propertyName;
}
