package com.avispa.ecm.model.type;

import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.util.exception.RepositoryCorruptionError;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface TypeRepository extends EcmObjectRepository<Type> {
    default Type findByTypeName(String typeName) throws RepositoryCorruptionError {
        return findByObjectName(typeName).orElseThrow(RepositoryCorruptionError::new);
    }

    Optional<Type> findByEntityClass(Class<?> clazz);

    default Type findByClass(Class<?> clazz) throws RepositoryCorruptionError {
        return findByEntityClass(clazz).orElseThrow(RepositoryCorruptionError::new);
    }
}
