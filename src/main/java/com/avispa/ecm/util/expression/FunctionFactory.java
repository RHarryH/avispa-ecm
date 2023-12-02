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

package com.avispa.ecm.util.expression;

import com.avispa.ecm.util.expression.function.Function;
import com.avispa.ecm.util.expression.function.impl.DateValue;
import com.avispa.ecm.util.expression.function.impl.Default;
import com.avispa.ecm.util.expression.function.impl.Pad;
import com.avispa.ecm.util.expression.function.impl.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class FunctionFactory {
    private static final String VALUE_FUNCTION = "value";
    private static final String DATEVALUE_FUNCTION = "datevalue";
    private static final String DEFAULT_FUNCTION = "default";
    private static final String PAD_FUNCTION = "pad";

    private FunctionFactory() {

    }

    public static String resolve(String functionName, String[] functionParams, Object object) {
        Function function;

        switch (functionName) {
            case VALUE_FUNCTION -> function = new Value();
            case DATEVALUE_FUNCTION -> function = new DateValue();
            case DEFAULT_FUNCTION -> function = new Default();
            case PAD_FUNCTION -> function = new Pad();
            default -> {
                log.error("Unknown function '{}'", functionName);
                return null;
            }
        }

        return resolveFunction(object, functionParams, function);
    }

    private static String resolveFunction(Object object, String[] functionParams, Function function) {
        String r = function.resolve(object, functionParams);

        return Matcher.quoteReplacement(r); // runs quoteReplacement to escape slashes and dollar characters
    }
}
