package com.avispa.ecm.model.context;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.EcmConfigObject;
import com.avispa.ecm.model.type.Type;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * Contexts importance (for the time being) is based on the insertion order. The context algorithm will search for all
 * contexts till it will find first matching one.
 *
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
public class Context extends EcmObject {
    @OneToMany
    private List<EcmConfigObject> ecmConfigObjects;

    @OneToOne
    private Type type;

    private String matchRule;
}
