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

package com.avispa.ecm.util.expression.function.impl;

import com.avispa.ecm.util.expression.function.ValueFunction;

/**
 * Extracts the value of provided property
 *
 * @author Rafał Hiszpański
 */
public class Value extends ValueFunction {
    @Override
    public String resolve(Object object, String[] params) {
        if(params.length < 1) {
            throw new IllegalArgumentException("Require one attribute");
        }

        return getValue(object, params[0]);
    }

    private String getValue(Object object, String propertyName) {
        Object value = extractValue(propertyName, object);

        return returnValue(propertyName, value);
    }
}
