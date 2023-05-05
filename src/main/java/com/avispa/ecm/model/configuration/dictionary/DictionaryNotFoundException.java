package com.avispa.ecm.model.configuration.dictionary;

public class DictionaryNotFoundException extends RuntimeException {
    public DictionaryNotFoundException(String dictionaryName) {
        super("Dictionary '" + dictionaryName + "' cannot be found");
    }
}