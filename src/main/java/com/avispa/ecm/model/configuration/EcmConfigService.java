package com.avispa.ecm.model.configuration;

import com.avispa.ecm.model.document.Document;

/**
 * @author Rafał Hiszpański
 */
public abstract class EcmConfigService<C extends EcmConfigObject> {
    public abstract void apply(C config, Document document);
}
