package com.avispa.ecm.model.configuration.propertypage.content.control;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Used for grouping controls for easier management with visibility and requirement conditions
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class Group extends Control {
    private String name;
    private List<Control> controls;
}
