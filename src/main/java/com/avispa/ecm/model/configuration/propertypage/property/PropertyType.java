package com.avispa.ecm.model.configuration.propertypage.property;

import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum PropertyType {
    LABEL("label"),
    CURRENCY("currency"),
    DATE("date"),
    DATETIME_LOCAL("datetime-local"),
    EMAIL("email"),
    NUMBER("number"),
    PASSWORD("password"),
    TEXT("text"),
    SEPARATOR("hr");

    private final String name;

    PropertyType(String name) {
        this.name = name;
    }
}
