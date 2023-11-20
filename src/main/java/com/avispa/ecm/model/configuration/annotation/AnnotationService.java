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

package com.avispa.ecm.model.configuration.annotation;

import com.avispa.ecm.util.exception.EcmException;
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

        if (null != classMemberField) {
            if(classMemberField.isAnnotationPresent(annotationClass)){
                return classMemberField.getAnnotation(annotationClass);
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("{} annotation not found for {} field", annotationClass.getSimpleName(), propertyName);
                }
                return null;
            }
        } else {
            log.error("There is no field {} in {} class", propertyName, objectClass);
            throw new EcmException("Can't determine display name for non existing '" + propertyName + "' property");
        }
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
                log.error("There is no field {} in {} class", individual, actualClass);
                throw new EcmException("Can't determine display name for non existing '" + individual + "' property");
            } else {
                actualClass = field.getType();
            }
        }

        return actualClass;
    }
}
