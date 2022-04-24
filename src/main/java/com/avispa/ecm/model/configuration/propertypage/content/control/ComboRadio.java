package com.avispa.ecm.model.configuration.propertypage.content.control;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class ComboRadio extends PropertyControl {
    private String typeName;
    private String dictionary;
    private boolean sortByLabel;

    @JsonIgnore
    private Map<String, String> values;
}
