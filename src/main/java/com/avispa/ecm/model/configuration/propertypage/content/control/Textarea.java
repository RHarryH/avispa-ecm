package com.avispa.ecm.model.configuration.propertypage.content.control;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class Textarea extends PropertyControl {
    private Integer rows;
    private Integer cols;

    private Integer minLength;
    private Integer maxLength;
}
