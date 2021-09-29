package com.avispa.cms.util.exception;

/**
 * @author Rafał Hiszpański
 */
public class CmsException extends Exception {
    public CmsException(String message) {
        super(message);
    }
    public CmsException(String message, Throwable cause) {
        super(message, cause);
    }
}
