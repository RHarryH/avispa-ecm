package com.avispa.ecm.util.expression.function;

import com.avispa.ecm.model.EcmObject;

/**
 * @author Rafał Hiszpański
 */
public interface Function {
    String resolve(EcmObject ecmObject, String[] params);
}
