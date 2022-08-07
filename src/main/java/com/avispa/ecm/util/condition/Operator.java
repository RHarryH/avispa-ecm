package com.avispa.ecm.util.condition;

import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum Operator {
    EQ("$eq"),
    NE("$ne"),
    GT("$gt"),
    GTE("$gte"),
    LT("$lt"),
    LTE("$lte");

    private final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }
}
