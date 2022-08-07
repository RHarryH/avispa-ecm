package com.avispa.ecm.util.condition.intermediate.value;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode
@ToString
public abstract class AbstractConditionValue<T> implements ConditionValue<T> {
    private final T value;

    AbstractConditionValue(@NonNull T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }
}
