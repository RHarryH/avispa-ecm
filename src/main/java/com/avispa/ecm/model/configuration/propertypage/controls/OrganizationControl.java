package com.avispa.ecm.model.configuration.propertypage.controls;

import com.avispa.ecm.model.configuration.propertypage.controls.converters.OrganizationControlTypeConverter;
import com.avispa.ecm.model.configuration.propertypage.controls.type.OrganizationControlType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
@DiscriminatorValue("organization_control")
public class OrganizationControl extends Control {
    @Convert(converter = OrganizationControlTypeConverter.class)
    @Column(name = "specific_control_type", nullable = false)
    private OrganizationControlType type;
}
