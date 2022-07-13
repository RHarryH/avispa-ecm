package com.avispa.ecm.model.configuration.dictionary;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.annotation.AnnotationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DictionaryService extends AnnotationService {
    private final EcmConfigRepository<Dictionary> dictionaryRepository;

    public Dictionary getDictionary(String dictionaryName) {
        return dictionaryRepository.findByObjectName(dictionaryName).orElseThrow(DictionaryNotFoundException::new);
    }

    @Override
    public String getValueFromAnnotation(Class<?> objectClass, String propertyName) {
        com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary dictionary =
                getFromAnnotation(com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary.class, objectClass, propertyName);
        return dictionary != null ? dictionary.name() : "";
    }
}
