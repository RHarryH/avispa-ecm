package com.avispa.ecm.model.document;

import com.avispa.ecm.model.EcmObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * This entity, similarly to Documentum approach, is used only to indicate
 * the purpose of all sub-objects
 *
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
public class Document extends EcmObject {
}
