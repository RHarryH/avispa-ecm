package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.callable.autolink.Autolink;
import com.avispa.ecm.model.configuration.load.dto.AutolinkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Rafał Hiszpański
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AutolinkMapper extends EcmConfigMapper<Autolink, AutolinkDto> {
    @Override
    @Mapping(source = "name", target = "objectName")
    @Mapping(target = "defaultValue", defaultValue = "Unknown")
    Autolink convertToEntity(AutolinkDto dto);

    @Override
    @Mapping(source = "name", target = "objectName")
    @Mapping(target = "defaultValue", defaultValue = "Unknown")
    void updateEntityFromDto(AutolinkDto dto, @MappingTarget Autolink entity);
}
