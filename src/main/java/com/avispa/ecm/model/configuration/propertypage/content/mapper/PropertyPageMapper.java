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
import com.avispa.ecm.model.configuration.dictionary.DictionaryService;
import com.avispa.ecm.model.configuration.dictionary.DictionaryValue;
import com.avispa.ecm.model.configuration.display.DisplayService;
import com.avispa.ecm.model.configuration.propertypage.PropertyPage;
import com.avispa.ecm.model.configuration.propertypage.content.PropertyPageContent;
import com.avispa.ecm.model.configuration.propertypage.content.control.Columns;
import com.avispa.ecm.model.configuration.propertypage.content.control.ComboRadio;
import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import com.avispa.ecm.model.configuration.propertypage.content.control.Group;
import com.avispa.ecm.model.configuration.propertypage.content.control.Hidden;
import com.avispa.ecm.model.configuration.propertypage.content.control.Label;
import com.avispa.ecm.model.configuration.propertypage.content.control.PropertyControl;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tab;
import com.avispa.ecm.model.configuration.propertypage.content.control.Table;
import com.avispa.ecm.model.configuration.propertypage.content.control.Tabs;
import com.avispa.ecm.model.content.Content;
import com.avispa.ecm.model.type.Type;
import com.avispa.ecm.model.type.TypeRepository;
import com.avispa.ecm.util.expression.ExpressionResolver;
import com.avispa.ecm.util.expression.ExpressionResolverException;
import com.avispa.ecm.util.reflect.PropertyUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PropertyPageMapper {
    private static final String OBJECT_NAME = "objectName";
    
    private final ExpressionResolver expressionResolver;
    private final TypeRepository typeRepository;
    private final ObjectMapper objectMapper;
    private final DictionaryService dictionaryService;
    private final DisplayService displayService;

    @PersistenceContext
    private EntityManager entityManager;

    public PropertyPageContent convertToContent(PropertyPage propertyPage, Object context, boolean readonly) {
        PropertyPageContent propertyPageContent = getPropertyPageContent(propertyPage.getPrimaryContent()).orElseThrow();
        propertyPageContent.setReadonly(readonly);
        propertyPageContent.setId(propertyPage.getId());

        processControls(propertyPageContent.getControls(), context);

        return propertyPageContent;
    }

    /**
     * Loads JSON content file and converts it to PropertyPageContent instance
     * @param content property page JSON content
     * @return
     */
    private Optional<PropertyPageContent> getPropertyPageContent(Content content) {
        try {
            if(null == content) {
                return Optional.empty();
            }

            byte[] resource = Files.readAllBytes(Path.of(content.getFileStorePath()));
            return Optional.of(objectMapper.readerFor(PropertyPageContent.class).withRootName("propertyPage").readValue(resource));
        } catch (IOException e) {
            log.error("Can't parse property page content from '{}'", content.getFileStorePath(), e);
        }

        return Optional.empty();
    }

    private void processControls(List<Control> controls, Object context) {
        for(Control control : controls) {
            if(control instanceof Columns) {
                Columns columns = (Columns) control;
                processControls(columns.getControls(), context);
            } else if(control instanceof Group) {
                Group group = (Group) control;
                processControls(group.getControls(), context);
            } else if(control instanceof Tabs) {
                Tabs tabs = (Tabs)control;
                for(Tab tab : tabs.getTabs()) {
                    processControls(tab.getControls(), context);
                }
            } else if(control instanceof Table) {
                Table table = (Table)control;

                Class<?> rowClass = getTableRowClass(table, context.getClass());
                if(null == rowClass) {
                    String errorMessage = String.format("Class of the table row can't be identified based on the '%s' property of '%s' context type.", table.getProperty(), context.getClass());
                    log.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
                table.setPropertyType(rowClass);

                processTableControls(table, context);
            } else {
                processControl(control, context);
            }
        }
    }

    /**
     * Identifies table data type. It uses following approach:
     * 1. Get field from context class of name defined in the table configuration
     * 2. Check if field is a list
     * 3. Extract info about type of elements in the list
     * 4. Found value is the table data type.
     * @param table
     * @param contextClass
     * @return
     */
    private Class<?> getTableRowClass(Table table, Class<?> contextClass) {
        Field field = ReflectionUtils.findField(contextClass, table.getProperty());
        if(field != null && field.getType().isAssignableFrom(List.class)) {
            java.lang.reflect.Type genericFieldType = field.getGenericType();

            if (genericFieldType instanceof ParameterizedType) {
                ParameterizedType aType = (ParameterizedType) genericFieldType;
                java.lang.reflect.Type[] fieldArgTypes = aType.getActualTypeArguments();
                if(fieldArgTypes.length > 0) {
                    Class<?> rowClass = (Class<?>) fieldArgTypes[0];
                    log.debug("Found table type class: '{}'", rowClass);
                    return rowClass;
                } else {
                    log.error("Type of the '{}' not found", table.getProperty());
                }
            }
        } else {
            log.error("Property '{}' not found in class '{}' or the property is not of the List type.", table.getProperty(), contextClass);
        }
        return null;
    }

    private void processTableControls(Table table, Object context) {
        table.getControls().stream()
                .filter(ComboRadio.class::isInstance)
                .map(ComboRadio.class::cast)
                .forEach(comboRadio -> {
                    if(StringUtils.isNotEmpty(comboRadio.getTypeName())) {
                        log.info("Type name for tables is ignored. Use dictionaries only.");
                        comboRadio.setTypeName("");
                    }
                    loadDictionary(comboRadio, table.getPropertyType());
                });

        // table controls are always required
        table.getControls()
                .forEach(control -> control.setRequired(true));

        // always add row id
        Hidden hidden = new Hidden();
        hidden.setProperty("id");
        table.getControls().add(hidden);

        fillTablePropertyValue(table, context);
    }

    private void fillTablePropertyValue(Table table, Object context) {
        String propertyName = table.getProperty(); // get property path

        // convert context object to tree representation and navigate to the node
        JsonNode root = objectMapper.valueToTree(context);

        int size = getTableSize(table, context);
        table.setSize(size);

        for (PropertyControl control : table.getControls()) {
            List<Object> row = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String jsonPtrExpression = "/" + propertyName.replace(".", "/") + "/" + i + "/" + control.getProperty();
                JsonNode node = root.at(jsonPtrExpression);

                if (node.isMissingNode()) {
                    log.warn("Value for {} property has bee not found", propertyName);
                    row.add(""); // empty
                } else if (node.isObject()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(OBJECT_NAME, node.get(OBJECT_NAME));
                    map.put("id", node.get("id"));
                    row.add(map);
                } else {
                    row.add(node.asText());
                }
            }
            control.setValue(row);
        }
    }

    private static int getTableSize(Table table, Object context) {
        int rows = 0;
        var value = PropertyUtils.getPropertyValue(context, table.getProperty());
        if(value instanceof Collection) {
            rows = ((Collection<?>) value).size();
        } else {
            log.warn("Property {} does not contain a collection of values", table.getProperty());
        }
        return rows;
    }

    private void processControl(Control control, Object context) {
        if(control instanceof Label) {
            Label label = (Label)control;
            try {
                label.setExpression(expressionResolver.resolve(context, label.getExpression()));
            } catch (ExpressionResolverException e) {
                log.error("Label expression couldn't be resolved", e);
            }
        } else if(control instanceof PropertyControl) {
            // TODO: validate if property is accessible?
            PropertyControl propertyControl = (PropertyControl) control;
            if(Strings.isEmpty(propertyControl.getLabel())) {
                propertyControl.setLabel(displayService.getDisplayValueFromAnnotation(context.getClass(), propertyControl.getProperty()));
            }

            if(control instanceof ComboRadio) {
                ComboRadio comboRadio = (ComboRadio) control;
                try {
                    if (StringUtils.isEmpty(comboRadio.getTypeName()) && StringUtils.isNotEmpty(comboRadio.getTypeNameExpression())) {
                        comboRadio.setTypeName(expressionResolver.resolve(context, comboRadio.getTypeNameExpression()));
                    }
                } catch (ExpressionResolverException e) {
                    log.error("Type name expression couldn't be resolved", e);
                }
                loadDictionary(comboRadio, context.getClass());
            }

            fillPropertyValue(propertyControl, context);
        }
    }

    private void fillPropertyValue(PropertyControl propertyControl, Object context) {
        String propertyName = propertyControl.getProperty(); // get property path

        // convert context object to tree representation and navigate to the node
        JsonNode root = objectMapper.valueToTree(context);
        JsonNode node = root.at("/" + propertyName.replace(".", "/"));

        if(node.isMissingNode()) {
            log.warn("Value for {} property has bee not found", propertyName);
            propertyControl.setValue(""); // empty
        } else if(node.isObject()) {
            Map<String, Object> map = new HashMap<>();
            map.put(OBJECT_NAME, node.get(OBJECT_NAME));
            map.put("id", node.get("id"));
            propertyControl.setValue(map);
        } else {
            propertyControl.setValue(node.asText());
        }
    }

    /**
     * Loads dictionary used by combo boxes and radio buttons
     * @param comboRadio
     * @param contextClass
     */
    private void loadDictionary(ComboRadio comboRadio, Class<?> contextClass) {
        if(StringUtils.isNotEmpty(comboRadio.getTypeName())) {
            loadValuesFromObject(comboRadio);
        } else {
            Dictionary dictionary = getDictionary(comboRadio, contextClass);
            loadValuesFromDictionary(comboRadio, dictionary);
        }
    }

    private void loadValuesFromObject(ComboRadio comboRadio) {
        Type type = typeRepository.findByTypeName(comboRadio.getTypeName());
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
        SimpleJpaRepository<? extends EcmObject, Long> jpaRepository = getSimpleRepository(type);
        return jpaRepository.findAll();
    }

    /**
     * Creates simple repository for specific type. This will create repositories even for the
     * abstract types allowing to get all concrete subtypes objects.
     * @param type type for which we want to get repository
     * @return
     */
    private SimpleJpaRepository<? extends EcmObject, Long> getSimpleRepository(Type type) {
        SimpleJpaRepository<? extends EcmObject, Long> jpaRepository;
        jpaRepository = new SimpleJpaRepository<>(type.getEntityClass(), entityManager);
        return jpaRepository;
    }

    private Dictionary getDictionary(ComboRadio comboRadio, Class<?> contextClass) {
        String dictionaryName = comboRadio.getDictionary();

        // if dictionary was not retrieved from property page, try with annotation
        if(StringUtils.isEmpty(dictionaryName)) {
            dictionaryName = dictionaryService.getDictionaryNameFromAnnotation(contextClass, comboRadio.getProperty());
        }

        // if dictionary name is still not resolved throw an exception
        if(StringUtils.isEmpty(dictionaryName)) {
            throw new IllegalStateException(
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