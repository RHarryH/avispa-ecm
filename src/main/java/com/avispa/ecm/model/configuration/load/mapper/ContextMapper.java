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

package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.context.Context;
import com.avispa.ecm.model.configuration.load.dto.ContextDto;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeService;
import com.avispa.ecm.util.exception.RepositoryCorruptionError;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ContextMapper implements EcmConfigMapper<Context, ContextDto> {
    @Autowired
    private EcmConfigRepository<EcmConfig> ecmConfigRepository;

    @Autowired
    private TypeService typeService;

    @Override
    @Mapping(source = "name", target = "objectName")
    @Mapping(source = "configNames", target = "ecmConfigs")
    @Mapping(target = "matchRule", defaultValue = "{}")
    public abstract Context convertToEntity(ContextDto dto);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    protected abstract List<EcmConfig> configNameToConfigObject(List<String> configNames);

    protected EcmConfig configNameToConfigObject(String configName) {
        return ecmConfigRepository.findByObjectName(configName).orElseThrow(() -> new RepositoryCorruptionError("Can't load configuration with following name: " + configName));
    }

    protected Type typeNameToType(String typeName) {
        return typeService.getType(typeName);
    }

    @Override
    @Mapping(source = "name", target = "objectName")
    @Mapping(source = "configNames", target = "ecmConfigs")
    @Mapping(target = "matchRule", defaultValue = "{}")
    public abstract void updateEntityFromDto(ContextDto dto, @MappingTarget Context entity);
}
