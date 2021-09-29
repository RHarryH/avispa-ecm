package com.avispa.cms.util.expression.function.impl;

import com.avispa.cms.model.document.Document;
import com.avispa.cms.util.expression.function.ValueFunction;
import com.avispa.cms.util.reflect.PropertyUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Function that gets property value and formats it using format provided as second
 * parameter
 *
 * @author Rafał Hiszpański
 */
@Slf4j
public class DateValue extends ValueFunction {
    @Override
    public String resolve(Document document, String[] params) {
        if(params.length < 2) {
            throw new IllegalArgumentException("Required two attributes");
        }

        return getValue(document, params[0], params[1]);
    }

    private String getValue(Document document, String propertyName, String format) {
        Object value = PropertyUtils.getPropertyValue(document, propertyName);

        if(value instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            return localDateTime.format(DateTimeFormatter.ofPattern(format));
        } else {
            log.warn("Value '{}' is not a date", value.toString());
        }

        return returnValue(propertyName, value);
    }
}
