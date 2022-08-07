package com.avispa.ecm.util.condition.intermediate.value;

import lombok.NonNull;

/**
 * @author Rafał Hiszpański
 */
class TextValue extends AbstractConditionValue<String> {
    TextValue(@NonNull String value) {
        super(value);
    }
}
