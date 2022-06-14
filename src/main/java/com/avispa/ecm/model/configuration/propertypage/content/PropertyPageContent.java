package com.avispa.ecm.model.configuration.propertypage.content;

import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import com.avispa.ecm.model.configuration.propertypage.content.control.Hidden;
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
    private String size = "normal";
    private List<Control> controls;

    /**
     * Adds hidden control on the root level of control hierarchy (can't be nested in lists, tables or even columns)
     * @param property
     */
    public void addHiddenControl(String property) {
        controls.add(new Hidden(property));
    }
}
