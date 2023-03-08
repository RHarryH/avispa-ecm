package com.avispa.ecm.model.configuration.load.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Data
public class ContextDto implements EcmConfigDto {
    private String name;
    private List<String> configNames;

    private String type;

    private String matchRule;

    private int importance;
}
