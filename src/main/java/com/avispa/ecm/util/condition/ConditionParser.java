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

package com.avispa.ecm.util.condition;

import com.avispa.ecm.util.condition.intermediate.Condition;
import com.avispa.ecm.util.condition.intermediate.ConditionGroup;
import com.avispa.ecm.util.condition.intermediate.Conditions;
import com.avispa.ecm.util.condition.intermediate.GroupType;
import com.avispa.ecm.util.condition.intermediate.misc.Misc;
import com.avispa.ecm.util.condition.intermediate.value.ConditionValue;
import com.avispa.ecm.util.json.JsonValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Component
@RequiredArgsConstructor
@Slf4j
public
class ConditionParser {
    private final JsonValidator jsonValidator;
    private final ObjectMapper objectMapper;

    /**
     * Parses JSON condition and converts it to an intermediate representation. The conditions are first validated against
     * JSON Schema and if a parsed string contains JSON Object.
     * @param conditions
     * @return
     */
    public Conditions parse(String conditions) {
        log.debug("Conditions: {}", conditions);

        boolean jsonValid = jsonValidator.validate(new ByteArrayInputStream(conditions.getBytes()), "/json-schemas/context/context-rule.json");
        if(!jsonValid) {
            log.error("Defined conditions does not have the valid structure");
            throw new IllegalStateException("Defined conditions does not have the valid structure");
        }

        JsonNode jsonTreeRoot = getJsonTree(conditions);
        validateNodeIsJsonObject(jsonTreeRoot);

        return parse(jsonTreeRoot);
    }

    /**
     * Converts conditions stored in string to JSON tree for further parsing
     * @param conditions
     * @return
     */
    private JsonNode getJsonTree(String conditions) {
        JsonNode jsonRoot;
        try {
            jsonRoot = objectMapper.readTree(conditions);
        } catch(IOException e) {
            log.error("Can't parse the conditions", e);
            throw new IllegalStateException("Can't parse the conditions", e);
        }
        return jsonRoot;
    }

    /**
     * Validates if provided node is a node containing JSON object
     * @param jsonNode
     */
    private void validateNodeIsJsonObject(JsonNode jsonNode) {
        if(!jsonNode.isObject()) { // in theory should not happen but is checked anyway
            log.error("Rule root is not a JSON object: {}", jsonNode.asText());
            throw new IllegalStateException("Rule root is not a JSON object");
        }
    }

    private Conditions parse(JsonNode jsonNode) {
        Conditions conditions = new Conditions();

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getKey().equals(Misc.LIMIT.getSymbol())) {
                conditions.setLimit(field.getValue().intValue());
                continue;
            }

            processField(field, conditions.getConditionGroup(), true);
        }

        return conditions;
    }

    private void processField(Map.Entry<String, JsonNode> jsonField, ConditionGroup conditionGroup, boolean isRoot) {
        String key = jsonField.getKey();
        JsonNode value = jsonField.getValue();

        log.debug("Field: key => {}, value => {}", key, value);

        if(value.isArray()) {
            ConditionGroup newConditionGroup = getConditionGroup(key, isRoot, conditionGroup);
            parseConditionGroup(value, newConditionGroup);
            /* If new condition group is the same as current one it means there was an occurrence of and group
             * on root level. In this case, and groups are flattened.
             */
            if(conditionGroup != newConditionGroup) {
                conditionGroup.addElement(newConditionGroup);
            }
        } else if(value.isObject()) {
            conditionGroup.addElement(parseConditions(key, value));
        } else {
            conditionGroup.addElement(Condition.equal(key, getConditionValue(value)));
        }
    }

    /**
     * Returns condition group object based on the key.
     * If the and group was found on the root level, default existing condition group is used.
     * @param key node key
     * @param isRoot whether the node is defined in the condition root
     * @param currentConditionGroup actual condition group, if isRoot equal true it is a default and condition group
     * @return
     */
    private ConditionGroup getConditionGroup(String key, boolean isRoot, ConditionGroup currentConditionGroup) {
        if(key.equals(GroupType.AND.getSymbol())) {
            return isRoot ? currentConditionGroup : ConditionGroup.and();
        } else if(key.equals(GroupType.OR.getSymbol())) {
            return ConditionGroup.or();
        } else {
            throw new IllegalStateException(String.format("Unknown symbol: %s", key));
        }
    }

    private void parseConditionGroup(JsonNode jsonNode, ConditionGroup conditionGroup) {
        for (JsonNode element : jsonNode) {
            Iterator<Map.Entry<String, JsonNode>> fields = element.fields();
            if (fields.hasNext()) {
                processField(fields.next(), conditionGroup, false);
            }
        }
    }

    private Condition parseConditions(String key, JsonNode value) {
        Iterator<Map.Entry<String, JsonNode>> fields = value.fields();
        if(fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String operatorSymbol = field.getKey();
            JsonNode actualValue = field.getValue();

            return Arrays.stream(Operator.values())
                    .filter(o -> o.getSymbol().equals(operatorSymbol))
                    .findFirst()
                    .map(o -> processValue(key, o, actualValue))
                    .orElseGet(() -> {
                        log.error("Can't process key '{}'", key);
                        return null;
                    });
        }

        log.error("No field defined for key '{}'", key);
        return null;
    }

    private Condition processValue(String key, Operator operator, JsonNode value) {
        ConditionValue<?> conditionValue = getConditionValue(value);
        return switch (operator) {
            case EQ -> Condition.equal(key, conditionValue);
            case NE -> Condition.notEqual(key, conditionValue);
            case GT -> Condition.greaterThan(key, conditionValue);
            case GTE -> Condition.greaterThanOrEqual(key, conditionValue);
            case LT -> Condition.lessThan(key, conditionValue);
            case LTE -> Condition.lessThanOrEqual(key, conditionValue);
            case LIKE -> Condition.like(key, conditionValue);
            case NOT_LIKE -> Condition.notLike(key, conditionValue);
        };
    }

    private ConditionValue<?> getConditionValue(JsonNode value) {
        JsonNodeType nodeType = value.getNodeType();
        switch (nodeType) {
            case STRING -> {
                return ConditionValue.text(value.textValue());
            }
            case BOOLEAN -> {
                return ConditionValue.bool(value.booleanValue());
            }
            case NUMBER -> {
                return ConditionValue.number(value.numberValue());
            }
            default -> log.debug("Unsupported node type: {}", nodeType);
        }
        return null;
    }
}