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

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.load.dto.PropertyPageDto;
import com.avispa.ecm.model.configuration.load.mapper.PropertyPageMapper;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.content.ContentService;
import com.avispa.ecm.util.exception.EcmConfigurationException;
import com.avispa.ecm.util.json.JsonValidator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Rafał Hiszpański
 */
@Component
class PropertyPageLoader extends GenericLoader<PropertyPage, PropertyPageDto, PropertyPageMapper> {
    private final JsonValidator jsonValidator;
    protected PropertyPageLoader(EcmConfigRepository<PropertyPage> ecmConfigRepository,
                               PropertyPageMapper ecmConfigMapper,
                               ContentService contentService,
                               JsonValidator jsonValidator) {
        super(ecmConfigRepository, ecmConfigMapper, contentService);
        this.jsonValidator = jsonValidator;
    }

    @Override
    protected boolean isValidContent(Path contentPath) {
        try (InputStream is = Files.newInputStream(contentPath)){
            return jsonValidator.validate(is, "/json-schemas/property-page-content.json");
        } catch (IOException e) {
            throw new EcmConfigurationException("Can't validate property page content", e);
        }
    }
}
