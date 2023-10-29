/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2023 Rafał Hiszpański
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.avispa.ecm.model;

import com.avispa.ecm.model.type.TypeService;
import com.avispa.ecm.util.exception.EcmException;
import com.avispa.ecm.util.exception.ResourceNotFoundException;
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
        EcmObject entity = ecmObjectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("The object '%s' does not exist", id)));
        String foundTypeName = typeService.getTypeName(entity.getClass());
        if(!foundTypeName.equals(typeName)) {
            throw new EcmException(String.format("The object '%s' is not an object of '%s' type", id, typeName));
        }

        return entity;
    }
}
