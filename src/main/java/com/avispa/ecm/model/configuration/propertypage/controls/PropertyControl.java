package com.avispa.ecm.model.configuration.propertypage.controls;

import com.avispa.ecm.model.configuration.propertypage.controls.converters.PropertyControlTypeConverter;
import com.avispa.ecm.model.configuration.propertypage.controls.type.PropertyControlType;
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
@DiscriminatorValue("property_control")
public class PropertyControl extends Control {
    private String name;

    @Convert(converter = PropertyControlTypeConverter.class)
    @Column(name = "specific_control_type", nullable = false)
    private PropertyControlType type;
}
