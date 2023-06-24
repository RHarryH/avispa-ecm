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

import com.avispa.ecm.util.condition.Operator;
import com.avispa.ecm.util.condition.intermediate.value.ConditionValue;
import lombok.Value;

/**
 * @author Rafał Hiszpański
 */
@Value
public class Condition implements IConditionElement {
    String key;
    Operator operator;
    ConditionValue<?> value;

    public static Condition equal(String key, ConditionValue<?> value) {
        return new Condition(key, Operator.EQ, value);
    }

    public static Condition notEqual(String key, ConditionValue<?> value) {
        return new Condition(key, Operator.NE, value);
    }

    public static Condition greaterThan(String key, ConditionValue<?> value) {
        return new Condition(key, Operator.GT, value);
    }

    public static Condition greaterThanOrEqual(String key, ConditionValue<?> value) {
        return new Condition(key, Operator.GTE, value);
    }

    public static Condition lessThan(String key, ConditionValue<?> value) {
        return new Condition(key, Operator.LT, value);
    }

    public static Condition lessThanOrEqual(String key, ConditionValue<?> value) {
        return new Condition(key, Operator.LTE, value);
    }
}
