package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.load.dto.DictionaryDto;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
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
    @Mapping(source = "name", target = "objectName")
    void updateEntityFromDto(DictionaryDto dto, @MappingTarget Dictionary entity);
}
