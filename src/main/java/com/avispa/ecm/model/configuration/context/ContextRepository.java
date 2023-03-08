package com.avispa.ecm.model.configuration.context;

import com.avispa.ecm.model.configuration.EcmConfigRepository;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public interface ContextRepository extends EcmConfigRepository<Context> {
    List<Context> findAllByOrderByImportanceDesc();
}
