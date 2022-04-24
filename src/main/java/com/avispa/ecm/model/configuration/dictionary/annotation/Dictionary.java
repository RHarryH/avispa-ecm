package com.avispa.ecm.model.configuration.dictionary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify that annotated field should use dictionary in property page
 * when combo box or radio control is used. Can be helpful in other custom cases requiring
 * extraction of extra data from dictionary.
 * @author Rafał Hiszpański
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Dictionary {
    String name();
}
