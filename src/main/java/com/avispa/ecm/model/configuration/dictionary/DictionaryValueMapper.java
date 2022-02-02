package com.avispa.ecm.model.configuration.dictionary;

import com.avispa.ecm.model.EcmObjectRepository;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class DictionaryValueMapper {
    @Autowired
    private EcmObjectRepository<DictionaryValue> dictionaryValueRepository;

    public UUID convertToString(DictionaryValue value) {
        return value.getId();
    }

    public DictionaryValue convertToDictionaryValue(UUID value) {
        return dictionaryValueRepository.findById(value).orElseThrow();
    }
}
