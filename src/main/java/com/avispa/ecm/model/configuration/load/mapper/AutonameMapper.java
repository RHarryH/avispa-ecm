package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.callable.autoname.Autoname;
import com.avispa.ecm.model.configuration.load.dto.AutonameDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Rafał Hiszpański
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AutonameMapper extends EcmConfigMapper<Autoname, AutonameDto> {
    @Override
    @Mapping(source = "name", target = "objectName")
    Autoname convertToEntity(AutonameDto dto);

    @Override
    @Mapping(source = "name", target = "objectName")
    void updateEntityFromDto(AutonameDto dto, @MappingTarget Autoname entity);
}
