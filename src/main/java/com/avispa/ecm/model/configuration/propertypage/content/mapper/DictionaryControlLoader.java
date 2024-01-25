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

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.dictionary.Dictionary;
import com.avispa.ecm.model.configuration.dictionary.DictionaryNotFoundException;
import com.avispa.ecm.model.configuration.dictionary.DictionaryService;
import com.avispa.ecm.model.configuration.dictionary.DictionaryValue;
import com.avispa.ecm.model.configuration.propertypage.content.control.ComboRadio;
import com.avispa.ecm.model.configuration.propertypage.content.control.dictionary.DictionaryLoad;
import com.avispa.ecm.model.configuration.propertypage.content.control.dictionary.DynamicLoad;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeService;
import com.avispa.ecm.util.condition.ConditionService;
import com.avispa.ecm.util.expression.ExpressionResolver;
import com.avispa.ecm.util.expression.ExpressionResolverException;
import jakarta.transaction.Transactional;
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
public class DictionaryControlLoader {
    private final DictionaryService dictionaryService;
    private final TypeService typeService;

    private final ConditionService conditionService;
    private final ExpressionResolver expressionResolver;

    /**
     * Loads dictionary used by combo boxes and radio buttons
     * @param comboRadio combo or radio button configuration
     * @param context context used to resolve expressions
     */
    @Transactional
    public Map<String, String> loadDictionary(ComboRadio comboRadio, Object context) {
        var settings = comboRadio.getLoadSettings();

        if (comboRadio.getLoadSettings() instanceof DynamicLoad dynamic) {
            return loadDynamicDictionary(dynamic, context);
        } else if (settings == null || settings instanceof DictionaryLoad) {
            var dictionaryLoad = (DictionaryLoad) settings;
            Dictionary dictionary = getDictionary(dictionaryLoad, comboRadio.getProperty(), context instanceof Class ? (Class<?>) context : context.getClass());
            return loadStaticDictionary(dictionaryLoad, dictionary);
        } else {
            throw new IllegalStateException("Dictionary configuration is unknown");
        }
    }

    /**
     * Load dynamic dictionary by providing configuration and a context object used to resolve expressions withing the
     * qualification
     *
     * @param dynamic configuration of dynamic load
     * @param context context used to resolve expressions
     * @return
     */
    public Map<String, String> loadDynamicDictionary(DynamicLoad dynamic, Object context) {
        Type type = typeService.getType(dynamic.getType());
        if (null != type) {
            List<? extends EcmObject> ecmObjects = conditionService.fetch(type.getEntityClass(), getQualification(dynamic, context));

            return ecmObjects.stream()
                    .filter(ecmObject -> StringUtils.isNotEmpty(ecmObject.getObjectName())) // filter out incorrect values with empty object name
                    .sorted(Comparator.comparing(EcmObject::getObjectName))
                    .collect(Collectors.toMap(ecmObject -> ecmObject.getId().toString(), EcmObject::getObjectName, (x, y) -> x, LinkedHashMap::new));
        }

        log.error("Type '{}' was not found", dynamic.getType());
        return Map.of();
    }

    private String getQualification(DynamicLoad dynamic, Object context) {
        String qualification = dynamic.getQualification();

        if (StringUtils.isEmpty(qualification)) {
            return "{}";
        }

        try {
            return expressionResolver.resolve(context, qualification);
        } catch (ExpressionResolverException e) {
            log.error("Can't parse the expressions in the qualification", e);
            throw new DictionaryNotFoundException("Dictionary can't be generated because there are problems with resolving the data");
        }
    }

    private Dictionary getDictionary(DictionaryLoad dictionaryLoad, String propertyName, Class<?> contextClass) {
        // if dictionary was not provided in configuration, try with annotation
        String dictionaryName = null == dictionaryLoad || StringUtils.isEmpty(dictionaryLoad.getDictionary()) ?
                dictionaryService.getDictionaryNameFromAnnotation(contextClass, propertyName) :
                dictionaryLoad.getDictionary();

        // if dictionary name is still not resolved throw an exception
        if(StringUtils.isEmpty(dictionaryName)) {
            throw new DictionaryNotFoundException(
                    String.format("Dictionary is not specified in property page configuration or using annotation in entity definition. Related property: '%s'", propertyName)
            );
        }

        return dictionaryService.getDictionary(dictionaryName);
    }

    private Map<String, String> loadStaticDictionary(DictionaryLoad dictionaryLoad, Dictionary dictionary) {
        log.debug("Loading values from {} dictionary", dictionary.getObjectName());

        boolean sortByLabel = null != dictionaryLoad && dictionaryLoad.isSortByLabel();

        return dictionary.getValues().stream()
                .filter(value -> StringUtils.isNotEmpty(value.getLabel())) // filter out incorrect values with empty object name
                .sorted(Comparator.comparing(sortByLabel ? DictionaryValue::getLabel : DictionaryValue::getKey))
                .collect(Collectors.toMap(DictionaryValue::getKey, DictionaryValue::getLabel, (x, y) -> x, LinkedHashMap::new));
    }
}
