package com.avispa.ecm.model.format;

/**
 * @author Rafał Hiszpański
 */
public class FormatNotFoundException extends Exception{
    public FormatNotFoundException(String message) {
        super(message);
    }

    public FormatNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
