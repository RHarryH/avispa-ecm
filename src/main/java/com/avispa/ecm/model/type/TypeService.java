package com.avispa.ecm.model.type;

import com.avispa.ecm.model.EcmObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Rafał Hiszpański
 */
@Service
@Slf4j
public class TypeService {
    public String getTypeDiscriminatorFromAnnotation(Class<? extends EcmObject> entityClass) {
        if (null != entityClass && entityClass.isAnnotationPresent(TypeDiscriminator.class)) {
            return entityClass.getAnnotation(TypeDiscriminator.class).name();
        }

        if(log.isWarnEnabled()) {
            log.warn("TypeDiscriminator annotation for {} EcmObject entity not found", entityClass);
        }

        return "";
    }
}
