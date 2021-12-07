package com.avispa.ecm.model.configuration.propertypage.content.control;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class Text extends PropertyControl {
    private String pattern;

    private Integer minLength;
    private Integer maxLength;
}
