package com.avispa.ecm.util;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class NestedObject extends EcmObject {
    public NestedObject(String objectName, String nestedField) {
        this.setObjectName(objectName);
        this.nestedField = nestedField;
    }
    @Dictionary(name = "TestDict")
    private String nestedField;
}
