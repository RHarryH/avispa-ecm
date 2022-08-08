package com.avispa.ecm.model.configuration.template;

import com.avispa.ecm.model.configuration.ContentLoadable;
import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.content.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
public final class TemplateService implements ContentLoadable {
    private final ContentService contentService;
    private final ResourceLoader resourceLoader;
    private final EcmConfigRepository<Template> ecmObjectRepository;

    @Override
    public void loadContentTo(String templateName, String sourceFileLocation) {
        Template template = ecmObjectRepository.findByObjectName(templateName).orElseThrow();
        Resource resource = resourceLoader.getResource(sourceFileLocation);

        contentService.loadContentTo(template, resource);
    }
}
