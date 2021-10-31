package com.avispa.ecm.model.configuration.propertypage.property;

import com.avispa.ecm.model.configuration.EcmConfigObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.MapKeyColumn;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Entity
@Getter
@Setter
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name="field_type")
public class Property extends EcmConfigObject {
    private String name;
    @Column(nullable = false)
    private String label;

    @Convert(converter = PropertyTypeConverter.class)
    @Column(nullable = false)
    private PropertyType type;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    private Map<String, String> attributes;

    @Column(columnDefinition = "boolean default false")
    private boolean required;
}
