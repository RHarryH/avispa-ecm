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
        return findByObjectNameIgnoreCase(typeName).orElseThrow(() -> new RepositoryCorruptionError("Can't find '" + typeName + "' type"));
    }

    Optional<Type> findByObjectNameIgnoreCase(String objectName);

    Optional<Type> findByEntityClass(Class<?> clazz);

    default Type findByClass(Class<?> clazz) throws RepositoryCorruptionError {
        return findByEntityClass(clazz).orElseThrow(() -> new RepositoryCorruptionError("Can't find type for '" + clazz.getSimpleName() + "' class"));
    }
}
