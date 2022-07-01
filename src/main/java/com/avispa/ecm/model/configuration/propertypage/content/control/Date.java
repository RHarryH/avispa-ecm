package com.avispa.ecm.model.configuration.propertypage.content.control;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class Date extends PropertyControl {
    private String min;
    private String max;
    private Integer step;
}
