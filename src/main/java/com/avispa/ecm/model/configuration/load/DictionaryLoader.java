package com.avispa.ecm.model.configuration.load;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.load.dto.DictionaryDto;
import com.avispa.ecm.model.configuration.load.mapper.DictionaryMapper;
import com.avispa.ecm.model.content.ContentService;
import org.springframework.stereotype.Component;

/**
 * @author Rafał Hiszpański
 */
@Component
class DictionaryLoader extends GenericLoader<Dictionary, DictionaryDto, DictionaryMapper> {
    protected DictionaryLoader(EcmConfigRepository<Dictionary> ecmConfigRepository, DictionaryMapper ecmConfigMapper, ContentService contentService) {
        super(ecmConfigRepository, ecmConfigMapper, contentService);
    }
}
