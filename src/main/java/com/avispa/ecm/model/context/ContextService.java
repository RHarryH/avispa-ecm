package com.avispa.ecm.model.context;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.configuration.EcmConfigObject;
import com.avispa.ecm.model.configuration.callable.CallableConfigObject;
import com.avispa.ecm.model.configuration.callable.CallableConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContextService {

    private final ContextRepository contextRepository;
    private final ObjectMapper objectMapper;

    private final List<CallableConfigService> callableConfigServices;

    /**
     * Automatically applies configurations of selected classes
     * @param object
     * @param configs
     * @param <T>
     * @param <C>
     */
    @SafeVarargs
    public final <T extends EcmObject, C extends CallableConfigObject> void applyMatchingConfigurations(T object, Class<? extends C>... configs) {
        List<Class<? extends C>> configsList = List.of(configs);

        Set<EcmConfigObject> availableConfigurations = getConfigurations(object).stream()
                .filter(e -> configsList.contains(e.getClass()))// filter only elements from the list
                .collect(Collectors.toSet());

        debugLog("Configuration services {}", callableConfigServices);

        for (CallableConfigService<CallableConfigObject> ecmConfigService : callableConfigServices) {
            Class<?> ecmConfigObject = getClassOfEcmConfigObjectSupportedByService(ecmConfigService);

            for (EcmConfigObject configObject : availableConfigurations) {
                if(configObject.getClass().equals(ecmConfigObject)) {

                    debugLog("{} object retrieved from the context is applicable for the service {}", ecmConfigService.getClass(), configObject.getClass().getSimpleName());

                    if (configsList.contains(configObject.getClass())) {
                        debugLog("Applying the configuration using {} service", ecmConfigService.getClass().getSimpleName());

                        ecmConfigService.apply((CallableConfigObject) configObject, object);
                    }
                }
            }
        }
    }

    /**
     * Get desired configurations applicable for all objects of specified type (matching rule is empty)
     * @param clazz object class
     * @param configurationType desired types of configuration
     * @param <T> object type
     * @param <C> configuration type
     * @return
     */
    public <T extends EcmObject, C extends EcmConfigObject> Optional<C> getConfiguration(Class<T> clazz, Class<C> configurationType) {
        return filter(getMatchingConfigurations(clazz), configurationType);
    }

    /**
     * Get desired configurations applicable for provided object (matching rule is empty)
     * @param object object for which we want to get configuration
     * @param configurationType desired types of configuration
     * @param <T> object type
     * @param <C> configuration type
     * @return
     */
    public <T extends EcmObject, C extends EcmConfigObject> Optional<C> getConfiguration(T object, Class<C> configurationType) {
        return filter(getMatchingConfigurations(object), configurationType);
    }

    /**
     * Filters returned configuration by desired types. If there are multiple configurations
     * only first one is returned.
     * @param stream
     * @param configurationType
     * @param <C>
     * @return
     */
    private <C extends EcmConfigObject> Optional<C> filter(Stream<EcmConfigObject> stream, Class<C> configurationType) {
        return stream.collect(Collectors.groupingBy(EcmConfigObject::getClass)) // group by class name
                .values().stream()
                .map(list -> list.get(0))// for each list get only first element
                .filter(configurationType::isInstance)
                .map(configurationType::cast)
                .findFirst();
    }

    /**
     * Returns list of configurations matching for provided object.
     * If there are more than one configuration of the same type (for example two autolinkings) only first one will
     * be returned.
     * The same configuration might occur in two cases:
     * - there are two configurations in the same context - order of insertion matters (TODO: are you sure?)
     * - there are two contexts having same configuration type - importance of context matters
     * (cared by {@link #getMatchingConfigurations(EcmObject)})
     * @param object object for which we want to find matching configuration
     * @param <T> type of object
     * @return
     */
    public <T extends EcmObject> List<EcmConfigObject> getConfigurations(T object) {
        return getMatchingConfigurations(object)
                .collect(Collectors.groupingBy(EcmConfigObject::getClass)) // group by class name
                .values().stream()
                .map(list -> list.get(0))// for each list get only first element
                .collect(Collectors.toList());
    }

    /**
     * Returns stream of configurations matching for provided object. It merges configurations from all matching
     * contexts.
     * @param object object for which we want to find matching configuration
     * @param <T> type of object
     * @return
     */
    @SuppressWarnings("java:S3864")
    private <T extends EcmObject> Stream<EcmConfigObject> getMatchingConfigurations(T object) {
        List<Context> contexts = contextRepository.findAllByOrderByImportanceAsc();

        return contexts.stream().filter(context -> matches(context, object))
                .peek(context -> {
                    if(log.isDebugEnabled()) {
                        log.debug("Matched context: '{}'", context.getObjectName());
                    }
                })
                .map(Context::getEcmConfigObjects)
                .flatMap(Collection::stream);
    }

    /**
     * Verifies if provided object matches to the context rules.
     * It checks if provided object is of a type defined in the context (or its subclass) and if object matching match
     * rule exists in the repository
     * @param context context, which rules are checked
     * @param object object checked against the configuration
     * @param <T> concrete type of EcmObject
     * @return true if context can be applied on the object
     */
    private <T extends EcmObject> boolean matches(Context context, T object) {
        Class<? extends EcmObject> contextSupportedClass = context.getType().getClazz();
        Class<? extends EcmObject> inputObjectClass = object.getClass();

        if(!contextSupportedClass.isAssignableFrom(inputObjectClass)) {
            log.info("{} is not {} or its subtype", inputObjectClass, contextSupportedClass);
            return false;
        }

        try {
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
            // convert JSON object to EcmObject of <T> type to eliminate fields not applicable to <T> type
            EcmObject queryObject = objectMapper.readValue(context.getMatchRule(), object.getClass());

            // convert to maps
            Map<String, Object> query = objectMapper.convertValue(queryObject, typeRef);
            Map<String, Object> reference = objectMapper.convertValue(object, typeRef);

            boolean matches = reference.entrySet().containsAll(query.entrySet());

            if(log.isDebugEnabled()) {
                log.debug("Query '{}' matches in '{}' reference: {}", query, reference, matches);
            }

            return matches;
        } catch (JsonProcessingException e) {
            log.error(context.getMatchRule());
            log.error("Error when trying to match object '{}}' with sample of '{}}'", object.getId(), context.getMatchRule(), e);
        }

        return false;
    }

    /**
     * Returns stream of configurations matching for provided object. It merges configurations from all matching
     * contexts. It returns only configuration applicable for all objects of specific type (matching
     * rule is empty)
     * @param clazz object for which we want to find matching configuration
     * @param <T> type of object
     * @return
     */
    private <T extends EcmObject> Stream<EcmConfigObject> getMatchingConfigurations(Class<T> clazz) {
        List<Context> contexts = contextRepository.findAllByOrderByImportanceAsc();

        return contexts.stream().filter(context -> context.getType().getClazz().equals(clazz))
                .filter(this::hasEmptyMatchRule)
                .map(Context::getEcmConfigObjects)
                .flatMap(Collection::stream);
    }

    private boolean hasEmptyMatchRule(Context context) {
        try {
            JsonNode actualObj = objectMapper.readTree(context.getMatchRule());
            return actualObj.isEmpty();
        } catch (JsonProcessingException e) {
            log.error("Can't parse {} context rule", context.getMatchRule(), e);
        }
        return false;
    }

    /**
     * Extracts configuration supported by provided service by checking what generic type is provided when
     * extending abstract class
     * @param callableConfigService
     * @return
     */
    private Class<?> getClassOfEcmConfigObjectSupportedByService(CallableConfigService<CallableConfigObject> callableConfigService) {
        ResolvableType callableConfigServiceType = ResolvableType.forClass(callableConfigService.getClass());
        Class<?> ecmConfigObject = callableConfigServiceType.getInterfaces()[0].getGeneric().resolve();
        if(null == ecmConfigObject) {
            throw new IllegalStateException(String.format("Can't evaluate object of %s service", callableConfigService.getClass().getSimpleName()));
        }
        debugLog( "{} is applicable for {} configuration object", ecmConfigObject, callableConfigService.getClass().getSimpleName());
        return ecmConfigObject;
    }

    private void debugLog(String message, Object... objects) {
        if (log.isDebugEnabled()) {
            log.debug(message, objects);
        }
    }
}
