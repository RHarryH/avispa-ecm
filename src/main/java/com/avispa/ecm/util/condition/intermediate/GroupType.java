package com.avispa.ecm.util.condition.intermediate;

import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum GroupType {
    AND("$and"),
    OR("$or");

    private final String symbol;

    GroupType(String symbol) {
        this.symbol = symbol;
    }
}
