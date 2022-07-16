package com.avispa.ecm.model.configuration.annotation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class AnnotationService {
    protected <A extends Annotation> A getFromAnnotation(Class<A> annotationClass, Class<?> objectClass, String propertyName) {
        Field classMemberField = getField(objectClass, propertyName);

        if (null != classMemberField && classMemberField.isAnnotationPresent(annotationClass)) {
            return classMemberField.getAnnotation(annotationClass);
        }

        if(log.isWarnEnabled()) {
            log.warn("{} annotation not found for {} field", annotationClass.getSimpleName(), propertyName);
        }

        return null;
    }

    private Field getField(Class<?> objectClass, String propertyName) {
        Field field = FieldUtils.getField(objectClass, propertyName, true);
        if(null == field && log.isWarnEnabled()) {
            log.warn("Property {} is not a member of {} class", propertyName, objectClass.getSimpleName());
        }

        return field;
    }
}
