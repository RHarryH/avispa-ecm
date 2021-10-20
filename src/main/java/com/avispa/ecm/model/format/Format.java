package com.avispa.ecm.model.format;

import com.avispa.ecm.model.EcmObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
public class Format extends EcmObject {
    public static final String PDF = "pdf";
    public static final String DOCX = "docx";
    public static final String ODT = "odt";

    private String description;
    private String mimeType;
    private String icon;

    public String getExtension() {
        return getObjectName();
    }

    public boolean isPdf() {
        return getExtension().equals(PDF);
    }
}
