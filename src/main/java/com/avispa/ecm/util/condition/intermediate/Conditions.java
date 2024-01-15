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

package com.avispa.ecm.util.condition.intermediate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Getter
@EqualsAndHashCode
@ToString
public class Conditions {
    private final ConditionGroup conditionGroup = ConditionGroup.and();
    @Setter
    private Integer limit;
    @Setter
    private Map<String, OrderDirection> orderBy = new HashMap<>();

    public enum OrderDirection {
        ASC,
        DESC
    }

    public void addElement(IConditionElement conditionElement) {
        conditionGroup.addElement(conditionElement);
    }

    public boolean isEmpty() {
        return conditionGroup.getConditions().isEmpty();
    }
}
