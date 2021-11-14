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
    public String resolve(EcmEntity ecmEntity, String[] params) {
        if(params.length < 1) {
            throw new IllegalArgumentException("Require one attribute");
        }

        return getValue(ecmEntity, params[0]);
    }

    private String getValue(EcmEntity ecmEntity, String propertyName) {
        Object value = PropertyUtils.getPropertyValue(ecmEntity, propertyName);

        return returnValue(propertyName, value);
    }
}
