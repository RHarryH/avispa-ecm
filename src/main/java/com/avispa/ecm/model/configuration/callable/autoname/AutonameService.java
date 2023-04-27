package com.avispa.ecm.model.configuration.callable.autoname;

import com.avispa.ecm.model.EcmObject;
import com.avispa.ecm.model.configuration.callable.CallableConfigService;
import com.avispa.ecm.util.expression.ExpressionResolver;
import com.avispa.ecm.util.expression.ExpressionResolverException;
import com.avispa.ecm.util.reflect.PropertyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

/**
 * Autonaming is a process of changing objects name by resolving expression provided in the autonaming configuration.
 *
 * @author Rafał Hiszpański
 */
@Service
@RequiredArgsConstructor
@Slf4j
public final class AutonameService implements CallableConfigService<Autoname> {

    private final ExpressionResolver expressionResolver;

    public void apply(Autoname autoname, EcmObject contextObject) {
        log.info("Autonaming '{}' for '{}' object has started", autoname, contextObject);

        String propertyName = autoname.getPropertyName();
        if(Strings.isEmpty(propertyName)) {
            log.error("Property name must be provided!");
        }

        try {
            String name = expressionResolver.resolve(contextObject, autoname.getRule());
            PropertyUtils.setPropertyValue(contextObject, propertyName, name);

            log.info("Autonaming '{}' for '{}' object has completed", autoname, contextObject);
        } catch (ExpressionResolverException e) {
            log.error("Autonaming '{}' for '{}' object couldn't be completed due to parse exception for the rule", autoname, contextObject, e);
        }
    }
}
