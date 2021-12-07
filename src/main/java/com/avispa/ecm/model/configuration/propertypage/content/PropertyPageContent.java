package com.avispa.ecm.model.configuration.propertypage.content;

import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class PropertyPageContent {
    private boolean readonly;
    private List<Control> controls;
}
