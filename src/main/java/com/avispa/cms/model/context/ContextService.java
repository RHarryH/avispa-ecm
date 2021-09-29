package com.avispa.cms.model.context;

import com.avispa.cms.model.CmsObject;
import com.avispa.cms.model.CmsObjectRepository;
import com.avispa.cms.model.configuration.CmsConfigObject;
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

    private final CmsObjectRepository<CmsObject> cmsObjectRepository;
    private final ContextRepository contextRepository;

    @Transactional
    public <T extends CmsObject> List<CmsConfigObject> getMatchingConfigurations(T object) {
        List<Context> contexts = contextRepository.findAll();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return contexts.stream().filter(context -> matches(context, object, objectMapper))
                .findFirst()
                .map(Context::getCmsConfigObjects)
                .orElse(Collections.emptyList());
    }

    private <T extends CmsObject> boolean matches(Context context, T object, ObjectMapper objectMapper) {
        Class<? extends CmsObject> contextSupportedClass = context.getType().getClazz();
        Class<? extends CmsObject> inputObjectClass = object.getClass();
        if(!inputObjectClass.isAssignableFrom(contextSupportedClass)) {
            return false;
        }

        try {
            Optional<CmsObject> value = cmsObjectRepository.findOne(
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
