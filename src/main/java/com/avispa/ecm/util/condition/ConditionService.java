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
