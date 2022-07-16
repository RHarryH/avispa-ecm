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
        Field classMemberField = PropertyUtils.getField(objectClass, propertyName);

        if (null != classMemberField && classMemberField.isAnnotationPresent(annotationClass)) {
            return classMemberField.getAnnotation(annotationClass);
        }

        if(log.isWarnEnabled()) {
            log.warn("{} annotation not found for {} field", annotationClass.getSimpleName(), propertyName);
        }

        return null;
    }
}
