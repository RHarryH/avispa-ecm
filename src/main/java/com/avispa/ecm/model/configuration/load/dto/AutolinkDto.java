package com.avispa.ecm.model.configuration.load.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Data
public class AutolinkDto implements EcmConfigDto {
    private String name;
    private List<String> rules = new ArrayList<>();
    private String defaultValue;
}
