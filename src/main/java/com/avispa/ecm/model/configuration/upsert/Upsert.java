package com.avispa.ecm.model.configuration.upsert;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
public class Upsert extends EcmConfig {
    @ManyToOne
    private PropertyPage propertyPage;
}
