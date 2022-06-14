package com.avispa.ecm.model;

import com.avispa.ecm.model.type.TypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
public class EcmObjectService {
    private final EcmObjectRepository<EcmObject> ecmObjectRepository;
    private final TypeService typeService;

    /**
     * Gets EcmObject instance from the repository and check if it is of a type
     * specified in argument
     * @param id id of EcmObject instance
     * @param typeName found EcmObject should be of a type provided here
     * @return
     */
    public EcmObject getEcmObjectFrom(UUID id, String typeName) {
        EcmObject entity = ecmObjectRepository.findById(id).orElseThrow();
        String foundTypeName = typeService.getTypeName(entity.getClass());
        if(!foundTypeName.equals(typeName)) {
            throw new IllegalStateException(String.format("The object '%s' is not an object of '%s' type", id, typeName));
        }

        return entity;
    }
}
