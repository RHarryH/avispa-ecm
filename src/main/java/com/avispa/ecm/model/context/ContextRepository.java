package com.avispa.ecm.model.context;

import com.avispa.ecm.model.EcmObjectRepository;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public interface ContextRepository extends EcmObjectRepository<Context> {
    List<Context> findAllByOrderByImportanceAsc();
}
