package com.avispa.ecm.model.context;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.type.Type;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.PositiveOrZero;
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
    @ManyToMany(fetch = FetchType.EAGER)
    private List<EcmConfig> ecmConfigs;

    @ManyToOne
    private Type type;

    @Column(nullable = false, columnDefinition = "varchar(255) default '{}'")
    private String matchRule;

    @Column(nullable = false)
    @PositiveOrZero
    private int importance; // higher = more important
}
