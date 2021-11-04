package com.avispa.ecm.model.configuration.propertypage.controls;

import com.avispa.ecm.model.EcmEntity;
import com.avispa.ecm.model.configuration.propertypage.controls.type.ControlType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKeyColumn;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="control_type")
public abstract class Control extends EcmEntity {
    @Column(nullable = false)
    private String label;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    private Map<String, String> attributes;

    @Column(columnDefinition = "boolean default false")
    private boolean required;

    public abstract ControlType getType();
}
