package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.load.dto.EcmConfigDto;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rafał Hiszpański
 */
@Transactional
public interface EcmConfigMapper<C extends EcmConfig, D extends EcmConfigDto> {
    C convertToEntity(D dto);

    void updateEntityFromDto(D dto, C entity);
}
