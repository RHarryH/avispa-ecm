package com.avispa.ecm.model.configuration.autoname;

import com.avispa.ecm.model.document.Document;
import com.avispa.ecm.util.expression.ExpressionResolver;
import com.avispa.ecm.util.reflect.PropertyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Autonaming is a process of changing objects name by resolving expression provided in the autonaming configuration.
 *
 * @author Rafał Hiszpański
 */
@Service
@Slf4j
public class AutonameService {

    private ExpressionResolver expressionResolver;

    @Autowired
    public AutonameService(ExpressionResolver expressionResolver) {
        this.expressionResolver = expressionResolver;
    }

    public void apply(Autoname autoname, Document contextDocument) {
        log.info("Autonaming '{}' for '{}' document has started", autoname, contextDocument);

        String propertyName = autoname.getPropertyName();
        if(Strings.isEmpty(propertyName)) {
            log.error("Property name must be provided!");
        }

        String name = expressionResolver.resolve(contextDocument, autoname.getRule());
        PropertyUtils.setPropertyValue(contextDocument, propertyName, name);

        log.info("Autonaming '{}' for '{}' document has completed", autoname, contextDocument);
    }
}
