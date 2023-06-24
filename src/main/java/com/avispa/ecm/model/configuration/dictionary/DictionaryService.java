/*
 * Avispa ECM - a small framework for implementing basic ECM solution
 * Copyright (C) 2023 Rafał Hiszpański
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
