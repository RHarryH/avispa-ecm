package com.avispa.ecm.model.type;

import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.util.exception.RepositoryCorruptionError;
import org.springframework.stereotype.Repository;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface TypeRepository extends EcmObjectRepository<Type> {
    default Type findByTypeName(String typeName) throws RepositoryCorruptionError {
        return findByObjectName(typeName).orElseThrow(RepositoryCorruptionError::new);
    }
}
