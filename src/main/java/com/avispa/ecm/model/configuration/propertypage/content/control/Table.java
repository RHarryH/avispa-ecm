package com.avispa.ecm.model.configuration.propertypage.content.control;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class Table extends PropertyControl {
    private List<Control> controls;
    @JsonIgnore
    private Class<?> propertyType;

    public Table() {
        this.setRequired(true); // table (and actually it's content) is always required
    }
}
