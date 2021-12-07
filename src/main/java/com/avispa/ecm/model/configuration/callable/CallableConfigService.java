package com.avispa.ecm.model.configuration.callable;

import com.avispa.ecm.model.EcmObject;

/**
 * @author Rafał Hiszpański
 */
public interface CallableConfigService<C extends CallableConfigObject> {
    void apply(C config, EcmObject object);
}
