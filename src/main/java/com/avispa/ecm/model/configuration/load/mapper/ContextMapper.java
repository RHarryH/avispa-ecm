package com.avispa.ecm.model.configuration.load.mapper;

import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.context.Context;
import com.avispa.ecm.model.configuration.load.dto.ContextDto;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeRepository;
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
    private TypeRepository typeRepository;

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    protected abstract List<EcmConfig> configNameToConfigObject(List<String> configNames);

    @Override
    @Mapping(source = "name", target = "objectName")
    @Mapping(source = "configNames", target = "ecmConfigs")
    @Mapping(target = "matchRule", defaultValue = "{}")
    public abstract Context convertToEntity(ContextDto dto);

    protected EcmConfig configNameToConfigObject(String configName) {
        return ecmConfigRepository.findByObjectName(configName).orElseThrow(RepositoryCorruptionError::new);
    }

    protected Type typeNameToType(String typeName) {
        return typeRepository.findByTypeName(typeName);
    }

    @Override
    @Mapping(source = "name", target = "objectName")
    @Mapping(source = "configNames", target = "ecmConfigs")
    @Mapping(target = "matchRule", defaultValue = "{}")
    public abstract void updateEntityFromDto(ContextDto dto, @MappingTarget Context entity);
}
