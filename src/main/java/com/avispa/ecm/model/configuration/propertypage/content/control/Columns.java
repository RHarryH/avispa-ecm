package com.avispa.ecm.model.configuration.propertypage.content.control;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class Columns extends Control {
    private List<Control> controls;
}
