package com.avispa.ecm.model.configuration.propertypage.controls.type;

import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum PropertyControlType implements ControlType {
    CURRENCY("currency"),
    DATE("date"),
    DATETIME_LOCAL("datetime-local"),
    EMAIL("email"),
    NUMBER("number"),
    PASSWORD("password"),
    TEXT("text");

    private final String name;

    PropertyControlType(String name) {
        this.name = name;
    }
}
