package com.avispa.ecm.model.configuration.load.dto;

import lombok.Data;

/**
 * @author Rafał Hiszpański
 */
@Data
public class UpsertDto implements EcmConfigDto {
    private String name;
    private String propertyPage;
}
