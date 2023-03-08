package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.load.dto.PropertyPageDto;
import com.avispa.ecm.model.configuration.load.mapper.PropertyPageMapper;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.content.ContentService;
import com.avispa.ecm.util.exception.EcmConfigurationException;
import com.avispa.ecm.util.json.JsonValidator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Rafał Hiszpański
 */
@Component
class PropertyPageLoader extends GenericLoader<PropertyPage, PropertyPageDto, PropertyPageMapper> {
    private final JsonValidator jsonValidator;
    protected PropertyPageLoader(EcmConfigRepository<PropertyPage> ecmConfigRepository,
                               PropertyPageMapper ecmConfigMapper,
                               ContentService contentService,
                               JsonValidator jsonValidator) {
        super(ecmConfigRepository, ecmConfigMapper, contentService);
        this.jsonValidator = jsonValidator;
    }

    @Override
    protected boolean isValidContent(Path contentPath) {
        try (InputStream is = Files.newInputStream(contentPath)){
            return jsonValidator.validate(is, "/json-schemas/property-page-content.json");
        } catch (IOException e) {
            throw new EcmConfigurationException("Can't validate property page content", e);
        }
    }
}
