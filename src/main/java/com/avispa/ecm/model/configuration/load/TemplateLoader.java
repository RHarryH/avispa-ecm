package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.load.dto.TemplateDto;
import com.avispa.ecm.model.configuration.load.mapper.TemplateMapper;
import com.avispa.ecm.model.configuration.template.Template;
import com.avispa.ecm.model.content.ContentService;
import org.springframework.stereotype.Component;

/**
 * @author Rafał Hiszpański
 */
@Component
class TemplateLoader extends GenericLoader<Template, TemplateDto, TemplateMapper> {
    protected TemplateLoader(EcmConfigRepository<Template> ecmConfigRepository, TemplateMapper ecmConfigMapper, ContentService contentService) {
        super(ecmConfigRepository, ecmConfigMapper, contentService);
    }
}
