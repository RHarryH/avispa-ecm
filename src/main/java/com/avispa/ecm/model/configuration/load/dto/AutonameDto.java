package com.avispa.ecm.model.configuration.load.dto;

import lombok.Data;

/**
 * @author Rafał Hiszpański
 */
@Data
public class AutonameDto implements EcmConfigDto {
    private String name;
    private String rule;
    private String propertyName;
}
