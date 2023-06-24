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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConditionService {
    private final ConditionParser conditionParser;
    private final ConditionResolver conditionResolver;
    private final ObjectMapper objectMapper;

    public boolean hasObjectMatching(String conditions, EcmObject object) {
        return conditionResolver.resolve(conditionParser.parse(conditions), object);
    }

    public boolean hasObjectOfClassMatching(String conditions, Class<? extends EcmObject> objectClass) {
        return conditionResolver.resolve(conditionParser.parse(conditions), objectClass);
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
