package com.avispa.ecm.model.context;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.EcmObjectRepository;
import com.avispa.ecm.model.configuration.callable.CallableConfigObject;
import com.avispa.ecm.model.configuration.EcmConfigObject;
import com.avispa.ecm.model.configuration.callable.CallableConfigService;
import com.avispa.ecm.model.document.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
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

    private final EcmObjectRepository<EcmObject> ecmObjectRepository;
    private final ContextRepository contextRepository;

    private final List<CallableConfigService> callableConfigServices;

    /**
     * Automatically applies configurations of selected classes
     * @param object
     * @param configs
     * @param <T>
     * @param <C>
     */
    @SafeVarargs
    public final <T extends Document, C extends CallableConfigObject> void applyMatchingConfigurations(T object, Class<? extends C>... configs) {
        List<Class<? extends C>> configsList = List.of(configs);

        Set<EcmConfigObject> availableConfigurations = getConfigurations(object).stream()
                .filter(e -> configsList.contains(e.getClass()))// filter only elements from the list
                .collect(Collectors.toSet());

        debugLog("Configuration services {}", callableConfigServices);

        for (CallableConfigService ecmConfigService : callableConfigServices) {
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

    public <T extends EcmObject, C extends EcmConfigObject> Optional<C> getConfiguration(T object, Class<C> configurationType) {
        return getMatchingConfigurations(object)
                .collect(Collectors.groupingBy(EcmConfigObject::getClass)) // group by class name
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
    private <T extends EcmObject> Stream<EcmConfigObject> getMatchingConfigurations(T object) {
        List<Context> contexts = contextRepository.findAllByOrderByImportanceDesc();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return contexts.stream().filter(context -> matches(context, object, objectMapper))
                .map(Context::getEcmConfigObjects)
                .flatMap(Collection::stream);
    }

    /**
     * Verifies if provided object matches to the context rules.
     * It checks if provided object is of a type defined in the context (or its subclass) and if object matching match
     * rule exists in the repository
     * @param context context, which rules are checked
     * @param object object checked against the configuration
     * @param objectMapper object mapper used to convert match rule to example of match object
     * @param <T> concrete type of EcmObject
     * @return true if context can be applied on the object
     */
    private <T extends EcmObject> boolean matches(Context context, T object, ObjectMapper objectMapper) {
        Class<? extends EcmObject> contextSupportedClass = context.getType().getClazz();
        Class<? extends EcmObject> inputObjectClass = object.getClass();

        if(!contextSupportedClass.isAssignableFrom(inputObjectClass)) {
            log.info("{} is not {} or its subtype", inputObjectClass, contextSupportedClass);
            return false;
        }

        try {
            EcmObject sampleObject = objectMapper.readValue(context.getMatchRule(), object.getClass());
            sampleObject.setId(object.getId());

            return ecmObjectRepository.exists(
                    Example.of(sampleObject)
            );
        } catch (JsonProcessingException e) {
            log.error(context.getMatchRule());
            log.error(String.format("Error when trying to match document '%s' with sample of '%s'", object.getId(), context.getMatchRule()), e);
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
