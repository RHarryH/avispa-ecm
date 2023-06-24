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

package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.load.dto.EcmConfigDto;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;

/**
 * Loader is responsible for loading single type of configuration. It should convert DTO object to specific entity and
 * upsert it to the database. If overwriting is enabled then existing configuration with the same name, values will be
 * just updated (there is no reloading of whole configuration). When content is provided it is assigned to the newly
 * loaded configuration.
 *
 * @author Rafał Hiszpański
 */
@Transactional
interface Loader<D extends EcmConfigDto> {
    /**
     * Loads configuration represented by DTO to the database.
     * @param configDto DTO representing the configuration
     * @param overwrite if there is existing configuration with the same object name then it will be replaced when this
     *                  parameter is set to true
     */
    void load(D configDto, boolean overwrite);

    /**
     * Loads configuration represented by DTO to the database. Additionally, attaches new content represented by a path.
     * It is also copied to the configured file store.
     * @param configDto DTO representing the configuration
     * @param contentPath path to the content file.
     * @param overwrite if there is existing configuration with the same object name then it will be replaced when this
     *                  parameter is set to true
     */
    void load(D configDto, Path contentPath, boolean overwrite);
}
