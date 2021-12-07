package com.avispa.ecm.util.expression.function;

/**
 * @author Rafał Hiszpański
 */
public interface Function {
    String resolve(Object object, String[] params);
}
