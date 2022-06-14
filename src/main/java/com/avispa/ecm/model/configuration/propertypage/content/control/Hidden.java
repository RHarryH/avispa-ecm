package com.avispa.ecm.model.configuration.propertypage.content.control;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class Hidden extends Control {
    private String property;

    public Hidden() {
        this.setType("hidden");
    }

    public Hidden(String property) {
        this();
        this.property = property;
    }
}
