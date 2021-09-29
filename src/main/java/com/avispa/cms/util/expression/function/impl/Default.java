package com.avispa.cms.util.expression.function.impl;

import com.avispa.cms.model.document.Document;
import com.avispa.cms.util.expression.function.Function;

/**
 * Provides default value in case of passed value is null
 *
 * @author Rafał Hiszpański
 */
public class Default implements Function {
    @Override
    public String resolve(Document document, String[] params) {
        if(params.length < 2) {
            throw new IllegalArgumentException("Required two attributes");
        }

        return getValue(params[0], params[1]);
    }

    private String getValue(String propertyValue, String defaultValue) {
        return null != propertyValue ? propertyValue : defaultValue;
    }
}
