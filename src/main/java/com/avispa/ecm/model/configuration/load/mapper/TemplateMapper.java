package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.load.dto.TemplateDto;
import com.avispa.ecm.model.configuration.template.Template;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author Rafał Hiszpański
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TemplateMapper extends EcmConfigMapper<Template, TemplateDto> {
    @Override
    @Mapping(source = "name", target = "objectName")
    Template convertToEntity(TemplateDto dto);

    @Override
    @Mapping(source = "name", target = "objectName")
    void updateEntityFromDto(TemplateDto dto, @MappingTarget Template entity);
}
