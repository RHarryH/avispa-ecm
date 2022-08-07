package com.avispa.ecm.util.condition.intermediate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Rafał Hiszpański
 */
@Getter
@EqualsAndHashCode
@ToString
public class Conditions {
    private final ConditionGroup conditionGroup = ConditionGroup.and();

    public void addElement(IConditionElement conditionElement) {
        conditionGroup.addElement(conditionElement);
    }
}
