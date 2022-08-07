package com.avispa.ecm.util.condition.intermediate.value;

import lombok.NonNull;

/**
 * @author Rafał Hiszpański
 */
class BooleanValue extends AbstractConditionValue<Boolean> {
    BooleanValue(@NonNull Boolean value) {
        super(value);
    }
}
