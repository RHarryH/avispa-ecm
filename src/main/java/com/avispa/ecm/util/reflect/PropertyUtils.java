package com.avispa.ecm.util.reflect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class PropertyUtils {

    private PropertyUtils() {

    }

    /**
     * Converts object to map where the key is a property name and value is the field value.
     *
     * It returns only fields, which have getter matching bean-standard.
     * @param object
     * @return
     * @throws Exception
     */
    public static Map<String, Object> introspect(Object object) {
        try {
            Map<String, Object> result = new HashMap<>();
            BeanInfo info = getBeanInfo(object);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Method reader = pd.getReadMethod();
                if (reader != null) {
                    Object value = getValue(object, reader);
                    result.put(pd.getName(), value);
                }
            }

            return result;
        } catch(IntrospectionException e) {
            log.error("Resolving properties has failed", e);
        }

        return Collections.emptyMap();
    }

    /**
     * Retrieves value of property in provided object.
     *
     * It returns only properties, which have getter matching bean-standard.
     * @param object object from which we want to retrieve the value
     * @param propertyName name of the property for which we want a value
     * @return value of the property
     * @throws Exception
     */
    public static Object getPropertyValue(Object object, String propertyName) {
        try {
            BeanInfo info = getBeanInfo(object);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Method reader = pd.getReadMethod();
                if (reader != null && !"class".equals(pd.getName()) && pd.getName().equals(propertyName)) {
                    return getValue(object, reader);
                }
            }
        } catch(IntrospectionException e) {
            log.error("Resolving '{}' property from object of '{}' class has failed", propertyName, object.getClass().getSimpleName(), e);
        }

        return null;
    }

    private static Object getValue(Object object, Method reader) {
        try {
            return reader.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Error when invoking method {}", reader.getName(), e);
        }
        return null;
    }

    /**
     * Sets value of property in provided object.
     *
     * It works only with classes, which have setter matching bean-standard.
     * @param object object to which we want to set the value
     * @param propertyName name of the property for which we want to set a value
     * @param propertyValue value to set
     * @throws Exception
     */
    public static void setPropertyValue(Object object, String propertyName, Object propertyValue) {
        try {
            BeanInfo info = getBeanInfo(object);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Method writer = pd.getWriteMethod();
                if (writer != null && !"class".equals(pd.getName()) && pd.getName().equals(propertyName)) {
                    setValue(object, propertyValue, writer);
                }
            }
        } catch(IntrospectionException e) {
            log.error("Resolving '{}' property from object of '{}' class has failed", propertyName, object.getClass().getSimpleName(), e);
        }
    }

    private static Object setValue(Object object, Object value, Method writer) {
        try {
            return writer.invoke(object, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Error when invoking method {}", writer.getName(), e);
        }
        return null;
    }

    public static boolean hasField(Class<?> clazz, String propertyName) {
        return null != getField(clazz, propertyName);
    }

    public static Field getField(Class<?> clazz, String propertyName) {
        Field field = FieldUtils.getField(clazz, propertyName, true);
        if(null == field && log.isWarnEnabled()) {
            log.warn("Property {} is not a member of {} class", propertyName, clazz.getSimpleName());
        }

        return field;
    }

    private static BeanInfo getBeanInfo(Object obj) throws IntrospectionException {
        return Introspector.getBeanInfo(obj.getClass());
    }
}
