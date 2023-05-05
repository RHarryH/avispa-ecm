package com.avispa.ecm.model.configuration.dictionary;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
import com.avispa.ecm.model.configuration.annotation.AnnotationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DictionaryService extends AnnotationService {
    private final EcmConfigRepository<Dictionary> dictionaryRepository;

    public String getValueFromDictionary(Class<?> objectClass, String propertyName, String propertyValue) {
        String dictionaryName = getDictionaryNameFromAnnotation(objectClass, propertyName);

        if(Strings.isEmpty(dictionaryName)) {
            log.info("There is no dictionary for '{}' property defined in '{}'", propertyName, objectClass.getSimpleName());
            return propertyValue;
        }

        Dictionary dictionary = getDictionary(dictionaryName);

        return dictionary.getValues().stream()
                .filter(dictionaryValue -> dictionaryValue.getKey().equals(propertyValue))
                .findFirst()
                .map(DictionaryValue::getLabel)
                .orElse(propertyValue);
    }

    public Dictionary getDictionary(Class<?> objectClass, String propertyName) {
        String dictionaryName = getDictionaryNameFromAnnotation(objectClass, propertyName);
        return getDictionary(dictionaryName);
    }

    public Dictionary getDictionary(String dictionaryName) {
        return dictionaryRepository.findByObjectName(dictionaryName).orElseThrow(() -> new DictionaryNotFoundException(dictionaryName));
    }

    public String getDictionaryNameFromAnnotation(Class<?> objectClass, String propertyName) {
        com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary dictionary =
                getFromAnnotation(com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary.class, objectClass, propertyName);
        return dictionary != null ? dictionary.name() : "";
    }
}
