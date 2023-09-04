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

package com.avispa.ecm.model.configuration.propertypage.content.control;

import com.avispa.ecm.model.configuration.propertypage.content.control.conditions.Conditions;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        //include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Columns.class, name = "columns"),
        @JsonSubTypes.Type(value = ComboRadio.class, name = "combo"),
        @JsonSubTypes.Type(value = Date.class, name = "date"),
        @JsonSubTypes.Type(value = Date.class, name = "datetime"),
        @JsonSubTypes.Type(value = Text.class, name = "email"),
        @JsonSubTypes.Type(value = Group.class, name = "group"),
        @JsonSubTypes.Type(value = Label.class, name = "label"),
        @JsonSubTypes.Type(value = Money.class, name = "money"),
        @JsonSubTypes.Type(value = Number.class, name = "number"),
        @JsonSubTypes.Type(value = Text.class, name = "password"),
        @JsonSubTypes.Type(value = ComboRadio.class, name = "radio"),
        @JsonSubTypes.Type(value = Separator.class, name = "separator"),
        @JsonSubTypes.Type(value = Table.class, name = "table"),
        @JsonSubTypes.Type(value = Tabs.class, name = "tabs"),
        @JsonSubTypes.Type(value = Text.class, name = "text"),
        @JsonSubTypes.Type(value = Textarea.class, name = "textarea"),
        @JsonSubTypes.Type(value = Hidden.class, name = "hidden")
})
public abstract class Control {
    private Conditions conditions;
}
