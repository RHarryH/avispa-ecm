package com.avispa.ecm.model.configuration.propertypage.controls.type;

import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum OrganizationControlType implements ControlType {
    LABEL("label"),
    SEPARATOR("separator");

    private final String name;

    OrganizationControlType(String name) {
        this.name = name;
    }
}
