package com.avispa.ecm.model.configuration.load.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Data
public class DictionaryDto implements EcmConfigDto {
    private String name;

    private String description;

    private List<DictionaryValueDto> values;
}
