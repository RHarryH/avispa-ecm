package com.avispa.ecm.model.configuration.dictionary;

import com.avispa.ecm.model.configuration.EcmConfigRepository;
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
public class DictionaryService {
    private final EcmConfigRepository<Dictionary> dictionaryRepository;

    public Dictionary getDictionary(String dictionaryName) {
        return dictionaryRepository.findByObjectName(dictionaryName).orElseThrow(DictionaryNotFoundException::new);
    }

    public String getDictionaryNameFromAnnotation(Class<?> objectClass, String propertyName) {
        Field classMemberField = getField(objectClass, propertyName);

        if (null != classMemberField && classMemberField.isAnnotationPresent(com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary.class)) {
            return classMemberField.getAnnotation(com.avispa.ecm.model.configuration.dictionary.annotation.Dictionary.class).name();
        }

        if(log.isWarnEnabled()) {
            log.warn("Dictionary annotation for {} field not found", propertyName);
        }

        return "";
    }

    private Field getField(Class<?> objectClass, String propertyName) {
        Field field = FieldUtils.getField(objectClass, propertyName, true);
        if(null == field && log.isWarnEnabled()) {
            log.warn("Property {} is not a member of {} class", propertyName, objectClass.getSimpleName());
        }

        return field;
    }
}
