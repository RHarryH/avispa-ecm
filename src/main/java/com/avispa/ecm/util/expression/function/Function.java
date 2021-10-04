package com.avispa.ecm.util.expression.function;

import com.avispa.ecm.model.document.Document;

/**
 * @author Rafał Hiszpański
 */
public interface Function {
    String resolve(Document document, String[] params);
}
