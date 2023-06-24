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
