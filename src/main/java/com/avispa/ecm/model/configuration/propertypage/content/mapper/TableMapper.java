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

import com.avispa.ecm.model.configuration.propertypage.content.control.ComboRadio;
import com.avispa.ecm.model.configuration.propertypage.content.control.Hidden;
import com.avispa.ecm.model.configuration.propertypage.content.control.PropertyControl;
import com.avispa.ecm.model.configuration.propertypage.content.control.Table;
import com.avispa.ecm.util.exception.EcmException;
import com.avispa.ecm.util.reflect.PropertyUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Component
@Slf4j
class TableMapper extends BaseControlsMapper<Table> {

    TableMapper(DictionaryControlLoader dictionaryControlLoader, ObjectMapper objectMapper) {
        super(dictionaryControlLoader, objectMapper);
    }

    public void processControl(Table table, Object context) {
        Class<?> tableRowClass = getTableRowClass(table, context.getClass());

        table.getControls().stream()
                .filter(ComboRadio.class::isInstance)
                .map(ComboRadio.class::cast)
                .forEach(comboRadio -> {
                    if(log.isDebugEnabled() && StringUtils.isNotEmpty(comboRadio.getTypeName())) {
                        log.debug("Type name for tables is ignored. Dictionary will be used if exists.");
                    }
                    dictionaryControlLoader.loadDictionary(comboRadio, tableRowClass);
                });

        // table controls are always required
        table.getControls()
                .forEach(control -> control.setRequired(true));

        // always add row id
        Hidden hidden = new Hidden();
        hidden.setProperty("id");
        table.getControls().add(hidden);

        fillPropertyValue(table, context);
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

            if (genericFieldType instanceof ParameterizedType parameterizedType) {
                java.lang.reflect.Type[] fieldArgTypes = parameterizedType.getActualTypeArguments();
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

        String errorMessage = String.format("Class of the table row can't be identified based on the '%s' property of '%s' context type.", table.getProperty(), contextClass);
        log.error(errorMessage);

        throw new EcmException(errorMessage);
    }

    private void fillPropertyValue(Table table, Object context) {
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
                    map.put(OBJECT_NAME, node.get(OBJECT_NAME).asText());
                    map.put("id", node.get("id").asText());
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
        if(value == null) {
            log.warn("Property '{}' is null. The size will be assumed to 0.", table.getProperty());
        } else if(value instanceof List) {
            rows = ((List<?>) value).size();
        } else {
            log.warn("Property '{}' does not contain a list of values", table.getProperty());
        }
        return rows;
    }
}
