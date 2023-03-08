package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.dictionary.DictionaryValue;
import com.avispa.ecm.model.configuration.load.dto.DictionaryValueDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Rafał Hiszpański
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public
interface DictionaryValueMapper extends EcmConfigMapper<DictionaryValue, DictionaryValueDto> {
    @Override
    @Mapping(source = "key", target = "objectName")
    DictionaryValue convertToEntity(DictionaryValueDto dto);

    @Override
    @Mapping(source = "key", target = "objectName")
    void updateEntityFromDto(DictionaryValueDto dto, @MappingTarget DictionaryValue entity);
}
