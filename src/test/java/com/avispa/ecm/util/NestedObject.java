package com.avispa.ecm.util;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
@Entity
public class NestedObject extends EcmObject {
    @Dictionary(name = "TestDict")
    private String nestedField;
}
