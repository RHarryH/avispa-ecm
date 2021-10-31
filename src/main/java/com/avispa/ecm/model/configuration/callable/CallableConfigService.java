package com.avispa.ecm.model.configuration.callable;

import com.avispa.ecm.model.document.Document;

/**
 * @author Rafał Hiszpański
 */
public interface CallableConfigService<C extends CallableConfigObject> {
    void apply(C config, Document document);
}
