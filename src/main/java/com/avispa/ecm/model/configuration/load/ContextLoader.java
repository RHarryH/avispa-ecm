package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.context.Context;
import com.avispa.ecm.model.configuration.load.dto.ContextDto;
import com.avispa.ecm.model.configuration.load.mapper.ContextMapper;
import com.avispa.ecm.model.content.ContentService;
import org.springframework.stereotype.Component;

/**
 * @author Rafał Hiszpański
 */
@Component
class ContextLoader extends GenericLoader<Context, ContextDto, ContextMapper> {
    protected ContextLoader(EcmConfigRepository<Context> ecmConfigRepository, ContextMapper ecmConfigMapper, ContentService contentService) {
        super(ecmConfigRepository, ecmConfigMapper, contentService);
    }
}
