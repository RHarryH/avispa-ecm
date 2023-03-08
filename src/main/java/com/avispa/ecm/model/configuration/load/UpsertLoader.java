package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.load.dto.UpsertDto;
import com.avispa.ecm.model.configuration.load.mapper.UpsertMapper;
import com.avispa.ecm.model.configuration.upsert.Upsert;
import com.avispa.ecm.model.content.ContentService;
import org.springframework.stereotype.Component;

/**
 * @author Rafał Hiszpański
 */
@Component
class UpsertLoader extends GenericLoader<Upsert, UpsertDto, UpsertMapper> {
    protected UpsertLoader(EcmConfigRepository<Upsert> ecmConfigRepository, UpsertMapper ecmConfigMapper, ContentService contentService) {
        super(ecmConfigRepository, ecmConfigMapper, contentService);
    }
}
