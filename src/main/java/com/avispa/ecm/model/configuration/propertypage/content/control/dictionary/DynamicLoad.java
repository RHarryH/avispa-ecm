/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2024 Rafał Hiszpański
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

package com.avispa.ecm.model.configuration.propertypage.content.control.dictionary;

import com.avispa.ecm.util.json.ConditionToStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class DynamicLoad implements LoadSettings {
    private String type;
    @JsonDeserialize(using = ConditionToStringDeserializer.class)
    private String qualification;

    public DynamicLoad(String type, String qualification) {
        this(type);
        this.qualification = qualification;
    }

    public DynamicLoad(String type) {
        this.type = type;
    }
}