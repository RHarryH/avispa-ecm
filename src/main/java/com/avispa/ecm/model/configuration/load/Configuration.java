package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.load.dto.EcmConfigDto;
import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Getter
@ToString
class Configuration {
    private final List<EcmConfigDto> configDtos = new ArrayList<>();
    private final Map<String, Path> contents = new HashMap<>();

    public void addConfigDto(EcmConfigDto config) {
        this.configDtos.add(config);
    }

    public void addContent(String objectName, Path contentPath) {
        this.contents.put(objectName, contentPath);
    }
}
