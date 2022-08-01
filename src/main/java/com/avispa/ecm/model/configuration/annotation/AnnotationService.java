package com.avispa.ecm.model.configuration.annotation;

import com.avispa.ecm.util.reflect.PropertyUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class AnnotationService {
    protected <A extends Annotation> A getFromAnnotation(Class<A> annotationClass, Class<?> objectClass, String propertyName) {
        String[] individualProperties = propertyName.split("\\.");
        String actualProperty = individualProperties[individualProperties.length - 1];
        Class<?> actualClass = getActualClass(objectClass, individualProperties);

        Field classMemberField = PropertyUtils.getField(actualClass, actualProperty);

        if (null != classMemberField && classMemberField.isAnnotationPresent(annotationClass)) {
            return classMemberField.getAnnotation(annotationClass);
        }

        if(log.isWarnEnabled()) {
            log.warn("{} annotation not found for {} field", annotationClass.getSimpleName(), propertyName);
        }

        return null;
    }

    /**
     * Crawls all individual properties to get the type of the last one
     * @param objectClass
     * @param individualProperties
     * @return
     */
    private Class<?> getActualClass(Class<?> objectClass, String[] individualProperties) {
        Class<?> actualClass = objectClass;

        for(int i = 0; i < individualProperties.length - 1; i++) { // iterate over individual properties except the last one
            String individual = individualProperties[i];
            Field field = PropertyUtils.getField(actualClass, individual);

            if(null == field) {
                log.error("Error");
            } else {
                actualClass = field.getType();
            }
        }

        return actualClass;
    }
}
