package com.avispa.ecm.model.configuration.template;

import com.avispa.ecm.model.configuration.ContentLoadable;
import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.content.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
public class TemplateService implements ContentLoadable {
    private final ContentService contentService;
    private final EcmConfigRepository<Template> ecmObjectRepository;

    @Override
    @Transactional
    public void loadContent(String templateName, String sourceFileLocation) {
        Template template = ecmObjectRepository.findByObjectName(templateName).orElseThrow();
        contentService.loadContentOf(template, sourceFileLocation);
    }
}
