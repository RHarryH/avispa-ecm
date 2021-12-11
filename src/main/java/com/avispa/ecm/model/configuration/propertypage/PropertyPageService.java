package com.avispa.ecm.model.configuration.propertypage;

import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.content.ContentService;
import com.avispa.ecm.util.json.JsonValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyPageService {
    private final ContentService contentService;
    private final ResourceLoader resourceLoader;
    private final EcmObjectRepository<PropertyPage> ecmObjectRepository;

    /**
     * Loads content of the property page. The content is validated against
     * JSON Schema
     * @param propertyPageName name of the property page object
     * @param sourceFileLocation location of the property page content file
     */
    public void loadContentTo(String propertyPageName, String sourceFileLocation) {
        loadContentTo(ecmObjectRepository.findByObjectName(propertyPageName).orElseThrow(), sourceFileLocation);
    }

    /**
     * Loads content of the property page. The content is validated against
     * JSON Schema
     * @param propertyPage property page object to which we want to attach the object
     * @param sourceFileLocation location of the property page content file
     */
    private void loadContentTo(PropertyPage propertyPage, String sourceFileLocation) {
        Resource resource = resourceLoader.getResource(sourceFileLocation);

        try {
            if(JsonValidator.validateJson(resource.getInputStream(), "/json-schemas/property-page-content.json")) {
                contentService.loadContentTo(propertyPage, resource);
            } else {
                log.error("Property page content wasn't loaded because it does not match JSON schema");
            }
        } catch (IOException e) {
            log.error("Can't load file from '{}'", sourceFileLocation);
        }
    }
}
