package com.avispa.ecm.util.expression.function.impl;

import com.avispa.ecm.model.EcmEntity;
import com.avispa.ecm.util.expression.function.ValueFunction;
import com.avispa.ecm.util.reflect.PropertyUtils;

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
        Object value = PropertyUtils.getPropertyValue(object, propertyName);

        return returnValue(propertyName, value);
    }
}
