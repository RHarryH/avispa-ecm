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
