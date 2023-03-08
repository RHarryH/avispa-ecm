package com.avispa.ecm.model.configuration.callable.autoname;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.callable.CallableConfigObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
public final class Autoname extends EcmConfig implements CallableConfigObject {
    @Column(nullable = false)
    private String rule;

    @Column(nullable = false)
    private String propertyName;
}
