package com.avispa.ecm.util.expression.function.impl;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.util.expression.function.ValueFunction;
import com.avispa.ecm.util.reflect.PropertyUtils;

/**
 * Extracts the value of provided property
 *
 * @author Rafał Hiszpański
 */
public class Value extends ValueFunction {
    @Override
    public String resolve(EcmObject ecmObject, String[] params) {
        if(params.length < 1) {
            throw new IllegalArgumentException("Require one attribute");
        }

        return getValue(ecmObject, params[0]);
    }

    private String getValue(EcmObject ecmObject, String propertyName) {
        Object value = PropertyUtils.getPropertyValue(ecmObject, propertyName);

        return returnValue(propertyName, value);
    }
}
