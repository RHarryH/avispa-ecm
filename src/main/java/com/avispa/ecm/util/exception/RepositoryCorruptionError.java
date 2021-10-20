package com.avispa.ecm.util.exception;

/**
 * @author Rafał Hiszpański
 */
public class RepositoryCorruptionError extends Error {
    public RepositoryCorruptionError() {

    }

    public RepositoryCorruptionError(String message) {
        super(message);
    }

    public RepositoryCorruptionError(String message, Throwable cause) {
        super(message, cause);
    }
}
