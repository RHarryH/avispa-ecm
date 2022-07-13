package com.avispa.ecm.model.configuration.display;

import com.avispa.ecm.model.configuration.annotation.AnnotationService;
import com.avispa.ecm.model.configuration.display.annotation.DisplayName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Service
public class DisplayService extends AnnotationService {
    @Override
    public String getValueFromAnnotation(Class<?> objectClass, String propertyName) {
        DisplayName displayName = getFromAnnotation(DisplayName.class, objectClass, propertyName);
        return displayName != null ? displayName.value() : propertyName;
    }
}
