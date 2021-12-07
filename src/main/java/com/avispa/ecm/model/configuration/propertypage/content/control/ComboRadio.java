package com.avispa.ecm.model.configuration.propertypage.content.control;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class ComboRadio extends PropertyControl {
    private String objectType;
    private Map<String, String> values;
}
