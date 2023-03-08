package com.avispa.ecm.util.exception;

/**
 * @author Rafał Hiszpański
 */
public class EcmConfigurationException extends EcmException {
    public EcmConfigurationException(String message) {
        super(message);
    }

    public EcmConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
