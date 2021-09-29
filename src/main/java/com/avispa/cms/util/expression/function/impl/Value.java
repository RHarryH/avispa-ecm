package com.avispa.cms.util.expression.function.impl;

import com.avispa.cms.model.document.Document;
import com.avispa.cms.util.expression.function.ValueFunction;
import com.avispa.cms.util.reflect.PropertyUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Extracts the value of provided property
 *
 * @author Rafał Hiszpański
 */
@Slf4j
public class Value extends ValueFunction {
    @Override
    public String resolve(Document document, String[] params) {
        if(params.length < 1) {
            throw new IllegalArgumentException("Required one attribute");
        }

        return getValue(document, params[0]);
    }

    private String getValue(Document document, String propertyName) {
        Object value = PropertyUtils.getPropertyValue(document, propertyName);

        return returnValue(propertyName, value);
    }
}
