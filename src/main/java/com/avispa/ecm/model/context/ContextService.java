package com.avispa.ecm.model.context;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.configuration.EcmConfigObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContextService {

    private final EcmObjectRepository<EcmObject> ecmObjectRepository;
    private final ContextRepository contextRepository;

    @Transactional
    public <T extends EcmObject> List<EcmConfigObject> getMatchingConfigurations(T object) {
        List<Context> contexts = contextRepository.findAll();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return contexts.stream().filter(context -> matches(context, object, objectMapper))
                .findFirst()
                .map(Context::getEcmConfigObjects)
                .orElse(Collections.emptyList());
    }

    private <T extends EcmObject> boolean matches(Context context, T object, ObjectMapper objectMapper) {
        Class<? extends EcmObject> contextSupportedClass = context.getType().getClazz();
        Class<? extends EcmObject> inputObjectClass = object.getClass();
        if(!inputObjectClass.isAssignableFrom(contextSupportedClass)) {
            return false;
        }

        try {
            Optional<EcmObject> value = ecmObjectRepository.findOne(
                    Example.of(
                            objectMapper.readValue(context.getMatchRule(), object.getClass())
                    )
            );

            return value.isPresent();
        } catch (JsonProcessingException e) {
            log.error(context.getMatchRule());
            log.error(String.format("Error when trying to match document '%s' with sample of '%s'", object.getId(), context.getMatchRule()), e);
        }

        return false;
    }
}
