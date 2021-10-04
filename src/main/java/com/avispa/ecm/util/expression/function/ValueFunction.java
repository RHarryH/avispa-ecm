package com.avispa.ecm.util.expression.function;

import lombok.extern.slf4j.Slf4j;

/**
 * Type of functions that extracts value from property name
 *
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class ValueFunction implements Function {

    protected final String returnValue(String propertyName, Object value) {
        if(null == value) {
            if(log.isDebugEnabled()) {
                log.debug("'{}' property value is null or property does not exist", propertyName);
            }

            return "";
        } else {
            if(log.isDebugEnabled()) {
                log.debug("'{}' property value found: {}", propertyName, value);
            }

            return value.toString();
        }
    }
}
