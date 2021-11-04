package com.avispa.ecm.util.reflect;

import com.avispa.ecm.model.EcmObject;
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

    public static Object getPropertyValue(EcmObject ecmObject, String propertyName) {
        // TODO: move to ReflectUtils
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(ecmObject.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName()) &&
                        pd.getName().equals(propertyName)) {
                    return pd.getReadMethod().invoke(ecmObject);
                }
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Resolving '{}' property for '{}' document has failed for following reason: {}", propertyName, ecmObject, e.getMessage());
        }

        return null;
    }

    public static void setPropertyValue(EcmObject ecmObject, String propertyName, Object propertyValue) {
        // TODO: move to ReflectUtils
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(ecmObject.getClass()).getPropertyDescriptors()) {
                if (pd.getWriteMethod() != null && !"class".equals(pd.getName()) &&
                        pd.getName().equals(propertyName)) {
                    pd.getWriteMethod().invoke(ecmObject, propertyValue);
                }
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Setting '{}' property for '{}' document has failed for following reason: {}", propertyName, ecmObject, e.getMessage());
        }
    }
}
