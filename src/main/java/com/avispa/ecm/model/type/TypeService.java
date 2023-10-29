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

package com.avispa.ecm.model.type;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.util.exception.RepositoryCorruptionError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * Service for registering and finding types in the ECM.
 *
 * @author Rafał Hiszpański
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TypeService {
    private final TypeRepository typeRepository;

    public static String getTypeDiscriminatorFromAnnotation(Class<? extends EcmObject> entityClass) {
        if (null != entityClass && entityClass.isAnnotationPresent(TypeDiscriminator.class)) {
            return entityClass.getAnnotation(TypeDiscriminator.class).name();
        }

        if(log.isWarnEnabled()) {
            log.warn("TypeDiscriminator annotation for {} EcmObject entity not found", entityClass);
        }

        return "";
    }

    public Type getType(String name) {
        try {
            return typeRepository.findByTypeName(name);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new RepositoryCorruptionError("The type name '" + name + "' matches to more than one result. Type names are case-insensitive.");
        }
    }

    public Optional<Type> findType(String name) {
        return typeRepository.findByObjectNameIgnoreCase(name);
    }

    public String getTypeName(Class<? extends EcmObject> entityClass) {
        return typeRepository.findByClass(entityClass).getObjectName();
    }

    /**
     * Register new type programmatically. This ensures the type name will always be stored in lower case.
     * @param type
     * @return
     */
    public Type registerType(Type type) {
        type.setObjectName(type.getObjectName().toLowerCase(Locale.ROOT));
        return typeRepository.save(type);
    }
}
