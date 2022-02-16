package com.avispa.ecm.model.configuration.propertypage.content.control;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public abstract class PropertyControl extends Control {
    private String label;
    private String property;
    private String customValidationFunction;
    private boolean required;
}
