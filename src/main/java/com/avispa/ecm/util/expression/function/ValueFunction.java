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

package com.avispa.ecm.util.expression.function;

import com.avispa.ecm.util.reflect.PropertyUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Type of functions that extracts value from property name
 *
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class ValueFunction implements Function {

    protected final Object extractValue(String propertyName, Object object) {
        if (object instanceof Map<?, ?> map) {
            return map.get(propertyName);
        } else {
            return PropertyUtils.getPropertyValue(object, propertyName);
        }
    }

    protected final String returnValue(String propertyName, Object value) {
        if(null == value) {
            log.debug("'{}' property value is null or property does not exist", propertyName);

            return "";
        } else {
            log.debug("'{}' property value found: {}", propertyName, value);

            return value.toString();
        }
    }
}
