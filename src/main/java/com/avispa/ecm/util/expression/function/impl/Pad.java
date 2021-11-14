package com.avispa.ecm.util.expression.function.impl;

import com.avispa.ecm.model.EcmEntity;
import com.avispa.ecm.util.expression.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Pads string to n characters in total using padding character or if not provided - default 0 value.
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
