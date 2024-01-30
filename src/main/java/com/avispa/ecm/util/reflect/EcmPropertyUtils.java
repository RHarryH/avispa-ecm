/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2023 Rafał Hiszpański
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.avispa.ecm.util.reflect;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EcmPropertyUtils {

    /**
     * Retrieves value of property in provided object.
     * <p>
     * It returns only properties, which have getter matching bean-standard.
     *
     * @param object       object from which we want to retrieve the value
     * @param propertyName name of the property for which we want a value
     * @return value of the property
     */
    public static Object getProperty(Object object, String propertyName) {
        try {
            return PropertyUtils.getProperty(object, propertyName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Resolving '{}' property from object of '{}' class has failed", propertyName, object.getClass().getSimpleName(), e);
        }

        return null;
    }

    /**
     * Sets value of property in provided object.
     * <p>
     * It works only with classes, which have setter matching bean-standard.
     *
     * @param object        object to which we want to set the value
     * @param propertyName  name of the property for which we want to set a value
     * @param propertyValue value to set
     */
    public static void setProperty(Object object, String propertyName, Object propertyValue) {
        try {
            PropertyUtils.setProperty(object, propertyName, propertyValue);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Resolving '{}' property from object of '{}' class has failed", propertyName, object.getClass().getSimpleName(), e);
        }
    }

    public static Map<String, Object> describe(Object object) {
        try {
            return PropertyUtils.describe(object);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Resolving properties has failed", e);
        }

        return Map.of();
    }

    /**
     * Get field object for specified field in specific class. Nested properties and fields in base classes are found.
     * Fields in arrays (tables) are not supported in contrary to properties retrieval.
     *
     * @param clazz     class for which we want to find field
     * @param fieldName name of the field
     * @return field object, null otherwise
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        String[] nestedProperties = splitProperty(fieldName);
        Class<?> currentClass = clazz;
        Field field = null;

        for (String nestedProperty : nestedProperties) {
            field = FieldUtils.getField(currentClass, nestedProperty, true);
            if (field != null) {
                // Update the current class for the next iteration
                currentClass = field.getType();
            } else {
                break;
            }
        }

        if (null == field && log.isWarnEnabled()) {
            log.warn("Property {} is not a member of {} class", fieldName, clazz.getSimpleName());
        }

        return field;
    }

    public static String[] splitProperty(String propertyName) {
        return propertyName.split("\\.");
    }
}
