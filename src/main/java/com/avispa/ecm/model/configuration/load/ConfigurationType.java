package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.load.dto.EcmConfigDto;
import lombok.Value;

/**
 * @author Rafał Hiszpański
 */
@Value(staticConstructor="of")
public class ConfigurationType {
    String name;
    Class<? extends EcmConfigDto> dto;
    boolean contentRequired;
}
