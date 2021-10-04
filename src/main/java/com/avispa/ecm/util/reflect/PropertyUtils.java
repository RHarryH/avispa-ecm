package com.avispa.ecm.util.reflect;

import com.avispa.ecm.model.document.Document;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class PropertyUtils {

    private PropertyUtils() {

    }

    public static Object getPropertyValue(Document document, String propertyName) {
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(document.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName()) &&
                        pd.getName().equals(propertyName)) {
                    return pd.getReadMethod().invoke(document);
                }
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Resolving '{}' property for '{}' document has failed for following reason: {}", propertyName, document, e.getMessage());
        }

        return null;
    }

    public static void setPropertyValue(Document document, String propertyName, Object propertyValue) {
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(document.getClass()).getPropertyDescriptors()) {
                if (pd.getWriteMethod() != null && !"class".equals(pd.getName()) &&
                        pd.getName().equals(propertyName)) {
                    pd.getWriteMethod().invoke(document, propertyValue);
                }
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Setting '{}' property for '{}' document has failed for following reason: {}", propertyName, document, e.getMessage());
        }
    }
}
