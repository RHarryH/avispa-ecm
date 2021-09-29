package com.avispa.cms.util.expression.function;

import com.avispa.cms.model.document.Document;

/**
 * @author Rafał Hiszpański
 */
public interface Function {
    String resolve(Document document, String[] params);
}
