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

import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.load.dto.DictionaryDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Rafał Hiszpański
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = DictionaryValueMapper.class)
public interface DictionaryMapper extends EcmConfigMapper<Dictionary, DictionaryDto> {
    @Override
    @Mapping(source = "name", target = "objectName")
    Dictionary convertToEntity(DictionaryDto dto);

    @Override
    @BeanMapping(qualifiedByName = "Update")
    @Mapping(source = "name", target = "objectName")
    void updateEntityFromDto(DictionaryDto dto, @MappingTarget Dictionary entity);

    @Named("Update")
    @BeforeMapping
    default void beforeMapping(@MappingTarget Dictionary entity) {
        entity.clearValues(); // clear values when performing and updates, otherwise they would be overwritten
    }
}
