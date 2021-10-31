package com.avispa.ecm.model.configuration.callable.autoname;

import com.avispa.ecm.model.configuration.callable.CallableConfigObject;
import com.avispa.ecm.model.configuration.EcmConfigObject;
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
public class Autoname extends EcmConfigObject implements CallableConfigObject {
    private String rule;
    private String propertyName;
}
