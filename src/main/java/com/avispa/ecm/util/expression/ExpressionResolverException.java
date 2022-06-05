package com.avispa.ecm.util.expression;

/**
 * @author Rafał Hiszpański
 */
public class ExpressionResolverException extends Exception {
    public ExpressionResolverException(String message) {
        super(message);
    }
    public ExpressionResolverException(String message, Throwable cause) {
        super(message, cause);
    }
}
