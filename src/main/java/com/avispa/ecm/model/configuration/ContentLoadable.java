package com.avispa.ecm.model.configuration;

/**
 * @author Rafał Hiszpański
 */
public interface ContentLoadable {
    void loadContent(String objectName, String sourceFileLocation);
}
