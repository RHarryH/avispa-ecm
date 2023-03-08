package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.callable.autolink.Autolink;
import com.avispa.ecm.model.configuration.load.dto.AutolinkDto;
import com.avispa.ecm.model.configuration.load.mapper.AutolinkMapper;
import com.avispa.ecm.model.content.ContentService;
import org.springframework.stereotype.Component;

/**
 * @author Rafał Hiszpański
 */
@Component
class AutolinkLoader extends GenericLoader<Autolink, AutolinkDto, AutolinkMapper> {
    protected AutolinkLoader(EcmConfigRepository<Autolink> ecmConfigRepository, AutolinkMapper ecmConfigMapper, ContentService contentService) {
        super(ecmConfigRepository, ecmConfigMapper, contentService);
    }
}
