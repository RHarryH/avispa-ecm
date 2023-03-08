package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.callable.autoname.Autoname;
import com.avispa.ecm.model.configuration.load.dto.AutonameDto;
import com.avispa.ecm.model.configuration.load.mapper.AutonameMapper;
import com.avispa.ecm.model.content.ContentService;
import org.springframework.stereotype.Component;

/**
 * @author Rafał Hiszpański
 */
@Component
class AutonameLoader extends GenericLoader<Autoname, AutonameDto, AutonameMapper> {

    protected AutonameLoader(EcmConfigRepository<Autoname> ecmConfigRepository, AutonameMapper ecmConfigMapper, ContentService contentService) {
        super(ecmConfigRepository, ecmConfigMapper, contentService);
    }
}
