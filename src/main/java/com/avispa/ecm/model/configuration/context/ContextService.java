package com.avispa.ecm.model.configuration.context;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.EcmConfig;
import com.avispa.ecm.model.configuration.callable.CallableConfigObject;
import com.avispa.ecm.model.configuration.callable.CallableConfigService;
import com.avispa.ecm.util.condition.ConditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
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

    private final ContextRepository contextRepository;

    private final ConditionService conditionService;
    private final List<CallableConfigService> callableConfigServices;

    public <T extends EcmObject, C extends CallableConfigObject> void applyMatchingConfigurations(T object, Class<? extends C> config) {
        applyMatchingConfigurations(object, List.of(config));
    }

    /**
     * Automatically applies configurations of selected classes
     * @param object
     * @param configs
     * @param <T>
     * @param <C>
     */
    @SuppressWarnings("unchecked")
    public <T extends EcmObject, C extends CallableConfigObject> void applyMatchingConfigurations(T object, List<Class<? extends C>> configs) {
        Set<EcmConfig> availableConfigurations = getConfigurations(object).stream()
                .filter(e -> configs.contains(e.getClass())) // filter only elements from the list
                .collect(Collectors.toSet());

        log.debug("Configuration services {}", callableConfigServices);

        for (CallableConfigService ecmConfigService : callableConfigServices) {
            Class<?> ecmConfigObject = getClassOfEcmConfigObjectSupportedByService(ecmConfigService);

            for (EcmConfig configObject : availableConfigurations) {
                if(configObject.getClass().equals(ecmConfigObject)) {

                    log.debug("{} object retrieved from the context is applicable for the service {}", ecmConfigService.getClass(), configObject.getClass().getSimpleName());

                    if (configs.contains(configObject.getClass())) {
                        log.debug("Applying the configuration using {} service", ecmConfigService.getClass().getSimpleName());

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
    public <T extends EcmObject, C extends EcmConfig> Optional<C> getConfiguration(Class<T> clazz, Class<C> configurationType) {
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
    public <T extends EcmObject, C extends EcmConfig> Optional<C> getConfiguration(T object, Class<C> configurationType) {
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
    private <C extends EcmConfig> Optional<C> filter(Stream<EcmConfig> stream, Class<C> configurationType) {
        return stream.collect(Collectors.groupingBy(EcmConfig::getClass)) // group by class name
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
    public <T extends EcmObject> List<EcmConfig> getConfigurations(T object) {
        return getMatchingConfigurations(object)
                .collect(Collectors.groupingBy(EcmConfig::getClass)) // group by class name
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
    private <T extends EcmObject> Stream<EcmConfig> getMatchingConfigurations(T object) {
        List<Context> contexts = contextRepository.findAllByOrderByImportanceDesc();

        return contexts.stream().filter(context -> matches(context, object))
                .peek(context -> log.debug("Matched context: '{}'", context.getObjectName()))
                .map(Context::getEcmConfigs)
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
        Class<? extends EcmObject> contextSupportedClass = context.getType().getEntityClass();
        Class<? extends EcmObject> inputObjectClass = object.getClass();

        if(!contextSupportedClass.isAssignableFrom(inputObjectClass)) {
            log.info("{} is not {} or its subtype", inputObjectClass, contextSupportedClass);
            return false;
        }

        boolean matches = conditionService.hasObjectMatching(context.getMatchRule(), object);

        log.debug("Object with id '{}' matches rule '{}': {}", object.getId(), context.getMatchRule(), matches);

        return matches;
    }

    /**
     * Returns stream of configurations matching for provided object. It merges configurations from all matching
     * contexts. It returns only configuration applicable for all objects of specific type (matching
     * rule is empty)
     * @param clazz object for which we want to find matching configuration
     * @param <T> type of object
     * @return
     */
    private <T extends EcmObject> Stream<EcmConfig> getMatchingConfigurations(Class<T> clazz) {
        List<Context> contexts = contextRepository.findAllByOrderByImportanceDesc();

        return contexts.stream().filter(context -> context.getType().getEntityClass().equals(clazz))
                .filter(context -> conditionService.isEmptyCondition(context.getMatchRule()))
                .map(Context::getEcmConfigs)
                .flatMap(Collection::stream);
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
        log.debug( "{} is applicable for {} configuration object", ecmConfigObject, callableConfigService.getClass().getSimpleName());
        return ecmConfigObject;
    }
}
