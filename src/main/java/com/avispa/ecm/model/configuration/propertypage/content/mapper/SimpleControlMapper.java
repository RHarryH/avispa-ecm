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
import com.avispa.ecm.model.configuration.propertypage.content.control.dictionary.DynamicLoad;
import com.avispa.ecm.util.expression.ExpressionResolver;
import com.avispa.ecm.util.expression.ExpressionResolverException;
import com.avispa.ecm.util.reflect.EcmPropertyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Component
@Slf4j
class SimpleControlMapper extends BaseControlsMapper<Control> {
    private final ExpressionResolver expressionResolver;
    private final DisplayService displayService;

    SimpleControlMapper(DictionaryControlLoader dictionaryControlLoader, ExpressionResolver expressionResolver, DisplayService displayService) {
        super(dictionaryControlLoader);
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
                if (comboRadio.getLoadSettings() instanceof DynamicLoad dynamicLoad) {
                    try {
                        dynamicLoad.setType(expressionResolver.resolve(context, dynamicLoad.getType()));
                    } catch (ExpressionResolverException e) {
                        log.error("Type name expression couldn't be resolved", e);
                    }
                }
                comboRadio.setOptions(dictionaryControlLoader.loadDictionary(comboRadio, context));
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

        propertyControl.setValue(getPropertyValueFromContext(context, propertyName));
    }

    /**
     * If property is on a blacklist of properties, it shouldn't be filled with a valie
     *
     * @param propertyName  property name
     * @param fillBlacklist black list of properties
     * @return
     */
    private static boolean isOnBlacklist(String propertyName, List<String> fillBlacklist) {
        String[] nestedProperties = EcmPropertyUtils.splitProperty(propertyName);
        String actualPropertyName = nestedProperties[nestedProperties.length - 1];
        if (fillBlacklist.contains(actualPropertyName)) {
            log.warn("Property {} is ignored and won't be filled with value", fillBlacklist);
            return true;
        }
        return false;
    }
}