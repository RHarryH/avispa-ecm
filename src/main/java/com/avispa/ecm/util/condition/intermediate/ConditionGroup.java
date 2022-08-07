package com.avispa.ecm.util.condition.intermediate;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Value
public class ConditionGroup implements IConditionElement {
    GroupType groupType;
    List<IConditionElement> conditions = new ArrayList<>();

    public static ConditionGroup and() {
        return new ConditionGroup(GroupType.AND);
    }

    public static ConditionGroup or() {
        return new ConditionGroup(GroupType.OR);
    }

    public ConditionGroup addElement(IConditionElement conditionElement) {
        conditions.add(conditionElement);
        return this;
    }
}
