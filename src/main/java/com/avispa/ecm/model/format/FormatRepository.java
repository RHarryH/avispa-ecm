package com.avispa.ecm.model.format;

import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.util.exception.RepositoryCorruptionError;
import org.springframework.stereotype.Repository;

/**
 * @author Rafał Hiszpański
 */
@Repository
public interface FormatRepository extends EcmObjectRepository<Format> {
    default Format findByExtension(String extension) throws RepositoryCorruptionError {
        return findByObjectName(extension).orElseGet(() -> findByObjectName("Default format").orElseThrow(RepositoryCorruptionError::new));
    }

    default Format findByExtensionOrThrowException(String extension) throws FormatNotFoundException {
        return findByObjectName(extension).orElseThrow(() -> new FormatNotFoundException("Can't find format: " + extension));
    }
}
