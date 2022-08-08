package com.avispa.ecm.model.configuration;

/**
 * @author Rafał Hiszpański
 */
public interface ContentLoadable {
    void loadContentTo(String objectName, String sourceFileLocation);
}
