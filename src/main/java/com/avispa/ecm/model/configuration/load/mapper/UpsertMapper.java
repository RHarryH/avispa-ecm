package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.load.dto.UpsertDto;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.configuration.upsert.Upsert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Rafał Hiszpański
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class UpsertMapper implements EcmConfigMapper<Upsert, UpsertDto> {
    @Autowired
    private EcmConfigRepository<PropertyPage> propertyPageRepository;

    @Override
    @Mapping(source = "name", target = "objectName")
    public abstract Upsert convertToEntity(UpsertDto dto);

    protected PropertyPage propertyPageNameToEntity(String propertyPageName) {
        return propertyPageRepository.findByObjectName(propertyPageName).orElseThrow();
    }

    @Override
    @Mapping(source = "name", target = "objectName")
    public abstract void updateEntityFromDto(UpsertDto dto, @MappingTarget Upsert entity);
}
