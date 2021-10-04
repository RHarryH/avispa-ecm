package com.avispa.ecm.util.exception;

/**
 * @author Rafał Hiszpański
 */
public class EcmException extends Exception {
    public EcmException(String message) {
        super(message);
    }
    public EcmException(String message, Throwable cause) {
        super(message, cause);
    }
}
