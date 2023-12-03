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

package com.avispa.ecm.model.configuration.propertypage.content.mapper;

import com.avispa.ecm.model.EcmEntity;
import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.dictionary.DictionaryNotFoundException;
import com.avispa.ecm.model.configuration.dictionary.DictionaryService;
import com.avispa.ecm.model.configuration.dictionary.DictionaryValue;
import com.avispa.ecm.model.configuration.propertypage.content.control.ComboRadio;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
@Component
@RequiredArgsConstructor
@Slf4j
class DictionaryControlLoader {
    private final DictionaryService dictionaryService;
    private final TypeService typeService;

    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Loads dictionary used by combo boxes and radio buttons
     * @param comboRadio
     * @param contextClass
     */
    public void loadDictionary(ComboRadio comboRadio, Class<?> contextClass) {
        if(StringUtils.isNotEmpty(comboRadio.getTypeName())) {
            loadValuesFromObject(comboRadio);
        } else {
            Dictionary dictionary = getDictionary(comboRadio, contextClass);
            loadValuesFromDictionary(comboRadio, dictionary);
        }
    }

    private void loadValuesFromObject(ComboRadio comboRadio) {
        Type type = typeService.getType(comboRadio.getTypeName());
        if (null != type) {
            List<? extends EcmObject> ecmObjects = getEcmObjects(type);

            Map<String, String> values = ecmObjects.stream()
                    .filter(ecmObject -> StringUtils.isNotEmpty(ecmObject.getObjectName())) // filter out incorrect values with empty object name
                    .sorted(Comparator.comparing(EcmObject::getObjectName))
                    .collect(Collectors.toMap(ecmObject -> ecmObject.getId().toString(), EcmObject::getObjectName, (x, y) -> x, LinkedHashMap::new));

            comboRadio.setOptions(values);
        } else {
            log.error("Type '{}' was not found", comboRadio.getTypeName());
        }
    }

    private List<? extends EcmObject> getEcmObjects(Type type) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EcmObject> cr = cb.createQuery(EcmObject.class);
        Root<? extends EcmObject> root = cr.from(type.getEntityClass());
        cr.select(root);

        TypedQuery<? extends EcmObject> query = entityManager.createQuery(cr);
        return query.getResultList();
    }

    private Dictionary getDictionary(ComboRadio comboRadio, Class<?> contextClass) {
        String dictionaryName = comboRadio.getDictionary();

        // if dictionary was not retrieved from property page, try with annotation
        if(StringUtils.isEmpty(dictionaryName)) {
            dictionaryName = dictionaryService.getDictionaryNameFromAnnotation(contextClass, comboRadio.getProperty());
        }

        // if dictionary name is still not resolved throw an exception
        if(StringUtils.isEmpty(dictionaryName)) {
            throw new DictionaryNotFoundException(
                    String.format("Dictionary is not specified in property page configuration or using annotation in entity definition. Related property: '%s'", comboRadio.getProperty())
            );
        }

        return dictionaryService.getDictionary(dictionaryName);
    }

    private void loadValuesFromDictionary(ComboRadio comboRadio, Dictionary dictionary) {
        log.debug("Loading values from {} dictionary", dictionary.getObjectName());

        Map<String, String> values = dictionary.getValues().stream()
                .filter(value -> StringUtils.isNotEmpty(value.getLabel())) // filter out incorrect values with empty object name
                .sorted(Comparator.comparing(EcmEntity::getObjectName))
                .collect(Collectors.toMap(DictionaryValue::getKey, DictionaryValue::getLabel, (x, y) -> x, LinkedHashMap::new));

        comboRadio.setOptions(values);
    }
}
