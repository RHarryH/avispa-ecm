package com.avispa.ecm.model.configuration.propertypage.content.control;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class ComboRadio extends PropertyControl {
    private String objectType;
    private String dictionary;

    @JsonIgnore
    private List<Map.Entry<UUID, String>> values;
}
