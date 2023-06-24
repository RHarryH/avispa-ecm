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

package com.avispa.ecm.util.condition.intermediate.value;

/**
 * @author Rafał Hiszpański
 */
public interface ConditionValue<T> {
    static ConditionValue<Boolean> bool(boolean value) {
        return new BooleanValue(value);
    }

    static ConditionValue<String> text(String value) {
        return new TextValue(value);
    }

    static ConditionValue<Number> number(Number value) {
        return new NumberValue(value);
    }

    T getValue();
}
