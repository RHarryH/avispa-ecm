package com.avispa.ecm.model.configuration.load.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Data
public class DictionaryValueDto implements EcmConfigDto {
    private String key;

    private Map<String, String> columns;

    private String label;

    @Override
    public String getName() {
        return this.key;
    }
}
