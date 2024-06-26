/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2023 Rafał Hiszpański
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.avispa.ecm.model.configuration.propertypage.content;

import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import com.avispa.ecm.model.configuration.propertypage.content.control.Hidden;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class PropertyPageContent {
    @JsonIgnore
    private UUID id;
    private PropertyPageContext context;
    private String size = "normal";
    private List<Control> controls;

    /**
     * Adds hidden control on the root level of control hierarchy (can't be nested in lists, tables or even columns).
     * It can be called only after successful mapping of property page configuration.
     *
     * @param property property name
     * @param value    value
     */
    public void addHiddenControl(String property, Object value) {
        if (controls == null) {
            throw new IllegalStateException("Map property page configuration to this object first");
        }

        Hidden hidden = new Hidden();
        hidden.setProperty(property);
        hidden.setValue(value);

        controls.add(hidden);
    }
}
