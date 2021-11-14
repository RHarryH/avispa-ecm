package com.avispa.ecm.util.expression.function;

import com.avispa.ecm.model.EcmEntity;

/**
 * @author Rafał Hiszpański
 */
public interface Function {
    String resolve(Object object, String[] params);
}
