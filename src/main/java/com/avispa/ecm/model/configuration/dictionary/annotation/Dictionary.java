package com.avispa.ecm.model.configuration.dictionary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation, which indicates to which Dictionary string value should be mapped to
 * For instance key KEY_1 might apply to more than one dictionary. Using this annotation
 * can guarantee that it will be mapped to correct DictionaryValue.
 * @author Rafał Hiszpański
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Dictionary {
    String name();
}
