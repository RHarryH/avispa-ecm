package com.avispa.ecm.model.configuration.propertypage.content.control.conditions;

import com.avispa.ecm.util.json.ConditionToStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class Conditions {
    @JsonDeserialize(using = ConditionToStringDeserializer.class)
    private String visibility;
    @JsonDeserialize(using = ConditionToStringDeserializer.class)
    private String requirement;
}
