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
import com.avispa.ecm.util.reflect.EcmPropertyUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class AnnotationService {
    protected <A extends Annotation> A getFromAnnotation(Class<A> annotationClass, Class<?> objectClass, String propertyName) {
        Field classMemberField = EcmPropertyUtils.getField(objectClass, propertyName);

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
}
