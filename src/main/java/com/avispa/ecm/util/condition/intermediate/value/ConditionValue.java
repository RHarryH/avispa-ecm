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
