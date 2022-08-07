package com.avispa.ecm.util.condition.intermediate.value;

import lombok.NonNull;

/**
 * @author Rafał Hiszpański
 */
class NumberValue extends AbstractConditionValue<Number> {
    NumberValue(@NonNull Number value) {
        super(value);
    }
}
