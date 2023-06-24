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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Pad left string to n characters in total using padding character or if not provided - default 0 value.
 * If number of characters will not be a correct positive integer then original input will be returned.
 * Sample:
 * ["1", "2"] => "01"
 * ["xyz", "8"] => "00000xyz"
 * ["abc", "4", "A"] => "Aabc"
 * ["3", "not-an-integer"] => "3"
 *
 * @author Rafał Hiszpański
 */
@Slf4j
public class Pad implements Function {
    @Override
    public String resolve(Object object, String[] params) {
        if(params.length < 2) {
            throw new IllegalArgumentException("Require at least two attributes");
        }

        int numberOfCharacters;
        try {
            numberOfCharacters = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            return params[0];
        }

        char paddingCharacter = getPaddingCharacter(params);

        return getValue(params[0], numberOfCharacters, paddingCharacter);
    }

    private char getPaddingCharacter(String[] params) {
        char paddingCharacter = '0';
        if(params.length == 3 && !params[2].isEmpty()) {
            paddingCharacter = params[2].charAt(0);
        }
        return paddingCharacter;
    }

    private String getValue(String propertyValue, int n, char paddingCharacter) {
        return StringUtils.leftPad(propertyValue, n, paddingCharacter);
    }
}
