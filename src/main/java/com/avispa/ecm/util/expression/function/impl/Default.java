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

import com.avispa.ecm.util.expression.function.Function;
import org.apache.commons.lang3.StringUtils;

/**
 * Provides default value in case of passed value is null
 *
 * @author Rafał Hiszpański
 */
public class Default implements Function {
    @Override
    public String resolve(Object object, String[] params) {
        if(params.length < 2) {
            throw new IllegalArgumentException("Require two attributes");
        }

        return getValue(params[0], params[1]);
    }

    private String getValue(String propertyValue, String defaultValue) {
        return StringUtils.isNotEmpty(propertyValue) ? propertyValue : defaultValue;
    }
}
