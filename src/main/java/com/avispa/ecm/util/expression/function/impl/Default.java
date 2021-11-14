package com.avispa.ecm.util.expression.function.impl;

import com.avispa.ecm.model.EcmEntity;
import com.avispa.ecm.util.expression.function.Function;
import org.apache.commons.lang3.StringUtils;

/**
 * Provides default value in case of passed value is null
 *
 * @author Rafał Hiszpański
 */
public class Default implements Function {
    @Override
    public String resolve(EcmEntity ecmEntity, String[] params) {
        if(params.length < 2) {
            throw new IllegalArgumentException("Require two attributes");
        }

        return getValue(params[0], params[1]);
    }

    private String getValue(String propertyValue, String defaultValue) {
        return StringUtils.isNotEmpty(propertyValue) ? propertyValue : defaultValue;
    }
}
