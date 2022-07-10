package com.avispa.ecm.model.configuration.callable.autoname;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.callable.CallableConfigObject;
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
public class Autoname extends EcmConfig implements CallableConfigObject {
    private String rule;
    private String propertyName;
}
