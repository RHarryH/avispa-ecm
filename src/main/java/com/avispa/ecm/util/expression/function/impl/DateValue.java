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
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Function that gets property value and formats it using format provided as second
 * parameter. Pattern used by this function matches Java 8 Data & Time patterns.
 *
 * @author Rafał Hiszpański
 */
@Slf4j
public class DateValue extends ValueFunction {
    @Override
    public String resolve(Object object, String[] params) {
        if(params.length < 2) {
            throw new IllegalArgumentException("Require two attributes");
        }

        return getValue(object, params[0], params[1]);
    }

    private String getValue(Object object, String propertyName, String format) {
        Object value = extractValue(propertyName, object);

        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.format(DateTimeFormatter.ofPattern(format));
        } else if (value instanceof LocalDate localDate) {
            return localDate.format(DateTimeFormatter.ofPattern(format));
        } else {
            log.warn("Value '{}' is not a date", value != null ? value.toString() : null);
        }

        return returnValue(propertyName, value);
    }
}
