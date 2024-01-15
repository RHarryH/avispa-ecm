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

import com.avispa.ecm.util.json.ConditionToStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Getter
@Setter
public class ComboRadio extends PropertyControl {
    private Dynamic dynamic;
    private Dictionary dictionary;

    private Map<String, String> options;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Dictionary {
        private String name;
        private boolean sortByLabel;

        public Dictionary(String name, boolean sortByLabel) {
            this(name);
            this.sortByLabel = sortByLabel;
        }

        public Dictionary(String name) {
            this.name = name;
        }
    }

    @Getter
    @Setter
    public static class Dynamic {
        private String typeName;
        private String typeNameExpression;
        @JsonDeserialize(using = ConditionToStringDeserializer.class)
        private String qualification;

        public static Dynamic ofTypeName(String typeName) {
            Dynamic dynamic = new Dynamic();
            dynamic.setTypeName(typeName);
            return dynamic;
        }

        public static Dynamic ofTypeNameExpression(String typeNameExpression) {
            Dynamic dynamic = new Dynamic();
            dynamic.setTypeNameExpression(typeNameExpression);
            return dynamic;
        }
    }
}
