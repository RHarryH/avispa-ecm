package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.load.dto.PropertyPageDto;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Rafał Hiszpański
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PropertyPageMapper extends EcmConfigMapper<PropertyPage, PropertyPageDto> {
    @Override
    @Mapping(source = "name", target = "objectName")
    PropertyPage convertToEntity(PropertyPageDto dto);

    @Override
    @Mapping(source = "name", target = "objectName")
    void updateEntityFromDto(PropertyPageDto dto, @MappingTarget PropertyPage entity);
}
