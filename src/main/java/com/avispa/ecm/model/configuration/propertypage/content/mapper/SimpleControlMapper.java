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

import com.avispa.ecm.model.configuration.display.DisplayService;
import com.avispa.ecm.model.configuration.propertypage.content.control.ComboRadio;
import com.avispa.ecm.model.configuration.propertypage.content.control.Control;
import com.avispa.ecm.model.configuration.propertypage.content.control.Label;
import com.avispa.ecm.model.configuration.propertypage.content.control.PropertyControl;
import com.avispa.ecm.util.expression.ExpressionResolver;
import com.avispa.ecm.util.expression.ExpressionResolverException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Component
@Slf4j
class SimpleControlMapper extends BaseControlsMapper<Control> {
    private final ExpressionResolver expressionResolver;
    private final DisplayService displayService;

    SimpleControlMapper(DictionaryControlLoader dictionaryControlLoader, ObjectMapper objectMapper, ExpressionResolver expressionResolver, DisplayService displayService) {
        super(dictionaryControlLoader, objectMapper);
        this.expressionResolver = expressionResolver;
        this.displayService = displayService;
    }

    public void processControl(Control control, List<String> fillBlacklist, Object context) {
        if (control instanceof Label label) {
            try {
                label.setExpression(expressionResolver.resolve(context, label.getExpression()));
            } catch (ExpressionResolverException e) {
                log.error("Label expression couldn't be resolved", e);
            }
        } else if (control instanceof PropertyControl propertyControl) {
            if(Strings.isEmpty(propertyControl.getLabel())) {
                propertyControl.setLabel(displayService.getDisplayValueFromAnnotation(context.getClass(), propertyControl.getProperty()));
            }

            if (control instanceof ComboRadio comboRadio) {
                try {
                    if (StringUtils.isEmpty(comboRadio.getTypeName()) && StringUtils.isNotEmpty(comboRadio.getTypeNameExpression())) {
                        comboRadio.setTypeName(expressionResolver.resolve(context, comboRadio.getTypeNameExpression()));
                    }
                } catch (ExpressionResolverException e) {
                    log.error("Type name expression couldn't be resolved", e);
                }
                dictionaryControlLoader.loadDictionary(comboRadio, context.getClass());
            }

            fillPropertyValue(propertyControl, fillBlacklist, context);
        }
    }

    private void fillPropertyValue(PropertyControl propertyControl, List<String> fillBlacklist, Object context) {
        String propertyName = propertyControl.getProperty(); // get property path

        if (isOnBlacklist(propertyName, fillBlacklist)) {
            propertyControl.setValue(""); // empty
            return;
        }

        // convert context object to tree representation and navigate to the node
        JsonNode root = objectMapper.valueToTree(context);
        JsonNode node = root.at("/" + propertyName.replace(".", "/"));

        if(node instanceof NullNode || node.isMissingNode()) {
            log.warn("Value for {} property has bee not found", propertyName);
            propertyControl.setValue(""); // empty
        } else if(node.isObject()) {
            Map<String, Object> map = new HashMap<>();
            map.put(OBJECT_NAME, node.get(OBJECT_NAME).asText());
            map.put("id", node.get("id").asText());
            propertyControl.setValue(map);
        } else {
            propertyControl.setValue(node.asText());
        }
    }

    /**
     * If property is on a blacklist of properties, it shouldn't be filled with a valie
     *
     * @param propertyName  property name
     * @param fillBlacklist black list of properties
     * @return
     */
    private static boolean isOnBlacklist(String propertyName, List<String> fillBlacklist) {
        String[] dismantledPropertyName = propertyName.split("\\.");
        String actualPropertyName = dismantledPropertyName[dismantledPropertyName.length - 1];
        if (fillBlacklist.contains(actualPropertyName)) {
            log.warn("Property {} is ignored and won't be filled with value", fillBlacklist);
            return true;
        }
        return false;
    }
}