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

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.util.condition.intermediate.Condition;
import com.avispa.ecm.util.condition.intermediate.Conditions;
import com.avispa.ecm.util.condition.intermediate.value.ConditionValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConditionService {
    private final ConditionParser conditionParser;
    private final ConditionRunner conditionRunner;
    private final ObjectMapper objectMapper;

    /**
     * Verify if context match rule finds object matching that context. If context rule is empty then everything is
     * matched, otherwise match happens when the match rule returns exactly one result.
     *
     * @param object     object, which is checked against the context match rule
     * @param conditions context match rule
     * @return
     */
    @Transactional
    public boolean hasContextMatch(EcmObject object, String conditions) {
        Conditions parsedConditions = conditionParser.parse(conditions);

        if (parsedConditions.isEmpty()) {
            return true;
        }
        // add test if match rule matches exactly provided object
        parsedConditions.addElement(Condition.equal("id", ConditionValue.text(object.getId().toString())));

        return conditionRunner.count(object.getClass(), parsedConditions) == 1;
    }

    /**
     * Verify if context match rule finds object matching that context. If context rule is empty then everything is
     * matched, otherwise match happens when the match rule returns at least one result.
     *
     * @param objectClass object type, which is checked against the context match rule
     * @param conditions  context match rule
     * @return
     */
    @Transactional
    public boolean hasContextMatch(Class<? extends EcmObject> objectClass, @NonNull String conditions) {
        Conditions parsedConditions = conditionParser.parse(conditions);
        return parsedConditions.isEmpty() ||
                conditionRunner.count(objectClass, parsedConditions) > 0;
    }

    /**
     * Return number of objects matching the provided conditions
     *
     * @param objectClass
     * @param conditions
     * @return
     */
    @Transactional
    public long count(Class<? extends EcmObject> objectClass, @NonNull String conditions) {
        return conditionRunner.count(objectClass, conditionParser.parse(conditions));
    }

    /**
     * Return objects matching the provided conditions
     *
     * @param objectClass
     * @param conditions
     * @return
     */
    @Transactional
    public <T extends EcmObject> List<T> fetch(Class<T> objectClass, @NonNull String conditions) {
        return conditionRunner.fetch(objectClass, conditionParser.parse(conditions));
    }

    public boolean isEmptyCondition(String condition) {
        try {
            JsonNode actualObj = objectMapper.readTree(condition);
            return actualObj.isEmpty();
        } catch (JsonProcessingException e) {
            log.error("Can't parse {} context condition", condition, e);
        }
        return false;
    }
}
