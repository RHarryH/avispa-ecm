package com.avispa.ecm.model.configuration.propertypage.content.control;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class Number extends ValidatablePropertyControl {
    private Float min;
    private Float max;
    private Float step;
}
