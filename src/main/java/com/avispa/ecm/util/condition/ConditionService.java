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

    public <T extends EcmObject> boolean parseConditions(String conditions, Class<T> inputObjectClass) {
        return conditionResolver.resolve(conditionParser.parse(conditions), inputObjectClass);
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
