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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Rafał Hiszpański
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TypeService {
    private final TypeRepository typeRepository;

    public String getTypeDiscriminatorFromAnnotation(Class<? extends EcmObject> entityClass) {
        if (null != entityClass && entityClass.isAnnotationPresent(TypeDiscriminator.class)) {
            return entityClass.getAnnotation(TypeDiscriminator.class).name();
        }

        if(log.isWarnEnabled()) {
            log.warn("TypeDiscriminator annotation for {} EcmObject entity not found", entityClass);
        }

        return "";
    }

    public Type getType(String name) {
        return typeRepository.findByTypeName(name);
    }

    public String getTypeName(Class<? extends EcmObject> entityClass) {
        return typeRepository.findByClass(entityClass).getObjectName();
    }
}
